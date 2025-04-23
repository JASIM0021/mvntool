package org.mvntool.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mvntool.cli.model.DependencyInfo;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DependencyDB {
    private static final Logger logger = Logger.getLogger(DependencyDB.class.getName());
    private static final String DB_URL = "https://raw.githubusercontent.com/JASIM0021/mvntool/refs/heads/main/dependencies.json";
    private static final Path LOCAL_DB_PATH = Path.of(System.getProperty("user.home"), ".mvntool", "dependencies.json");
    private static final long CACHE_TTL_MS = 24 * 60 * 60 * 1000; // 24 hours
    private static Map<String, Map<String, Object>> database = Collections.emptyMap();

    /**
     * Retrieves dependency information for the specified artifact and version
     * @param artifact The artifact name (key from dependencies.json)
     * @param version Version number or "latest"
     * @return DependencyInfo containing groupId, artifactId and resolved version
     * @throws IOException If database cannot be loaded
     * @throws RuntimeException If artifact or version not found
     */
    public static DependencyInfo get(String artifact, String version) throws IOException {
        loadDatabase();

        Map<String, Object> dep = database.get(artifact);
        if (dep == null) {
            throw new RuntimeException("Artifact '" + artifact + "' not found in database");
        }

        String resolvedVersion = resolveVersion(dep, version, artifact);
        validateDependencyFields(dep, artifact);

        return new DependencyInfo(
                dep.get("groupId").toString(),
                dep.get("artifactId").toString(),
                resolvedVersion
        );
    }

    private static String resolveVersion(Map<String, Object> dep, String version, String artifact) {
        if ("latest".equals(version)) {
            if (!dep.containsKey("latest")) {
                throw new RuntimeException("Artifact '" + artifact + "' doesn't specify a latest version");
            }
            return dep.get("latest").toString();
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, String> versions = (Map<String, String>) dep.get("versions");
            if (versions == null || versions.isEmpty()) {
                throw new RuntimeException("No versions available for artifact '" + artifact + "'");
            }

            String resolvedVersion = versions.get(version);
            if (resolvedVersion == null) {
                throw new RuntimeException("Version '" + version + "' not found for artifact '" + artifact +
                        "'. Available versions: " + versions.keySet());
            }
            return resolvedVersion;
        } catch (ClassCastException e) {
            throw new RuntimeException("Invalid version format for artifact '" + artifact + "'", e);
        }
    }

    private static void validateDependencyFields(Map<String, Object> dep, String artifact) {
        if (!dep.containsKey("groupId")) {
            throw new RuntimeException("Artifact '" + artifact + "' missing required field: groupId");
        }
        if (!dep.containsKey("artifactId")) {
            throw new RuntimeException("Artifact '" + artifact + "' missing required field: artifactId");
        }
    }

    private static synchronized void loadDatabase() throws IOException {
        if (database.isEmpty()) {
            try {
                ensureDatabaseExists();
                Map<String, Object> topLevel = new ObjectMapper().readValue(LOCAL_DB_PATH.toFile(), Map.class);

                @SuppressWarnings("unchecked")
                Map<String, Map<String, Object>> deps = (Map<String, Map<String, Object>>) topLevel.get("dependencies");

                if (deps == null) {
                    throw new IOException("Database format error: 'dependencies' field not found");
                }
                database = Collections.unmodifiableMap(deps);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to load dependency database", e);
                database = Collections.emptyMap();
                throw e;
            }
        }
    }

    private static void ensureDatabaseExists() throws IOException {
        boolean shouldDownload = !Files.exists(LOCAL_DB_PATH) ||
                isCacheExpired();

        if (shouldDownload) {
            try {
                downloadDatabase();
            } catch (IOException e) {
                if (!Files.exists(LOCAL_DB_PATH)) {
                    throw new IOException("Failed to download database and no local copy exists", e);
                }
                logger.log(Level.WARNING, "Database update failed, using cached version", e);
            }
        }
    }

    private static boolean isCacheExpired() throws IOException {
        try {
            long lastModified = Files.getLastModifiedTime(LOCAL_DB_PATH).toMillis();
            return System.currentTimeMillis() - lastModified > CACHE_TTL_MS;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Couldn't check cache expiry, forcing download", e);
            return true;
        }
    }

    private static void downloadDatabase() throws IOException {
        Files.createDirectories(LOCAL_DB_PATH.getParent());
        logger.info("Downloading dependency database from " + DB_URL);

        try (var in = new URL(DB_URL).openStream()) {
            Files.copy(in, LOCAL_DB_PATH, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Database successfully downloaded to " + LOCAL_DB_PATH);
        }
    }

    /**
     * Clears the in-memory cache, forcing a reload on next access
     */
    public static synchronized void clearCache() {
        database = Collections.emptyMap();
    }
}