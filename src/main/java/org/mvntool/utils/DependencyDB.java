package org.mvntool.utils;




import com.fasterxml.jackson.databind.ObjectMapper;
import org.mvntool.cli.model.DependencyInfo;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class DependencyDB {
    private static final String DB_URL = "https://yourdomain.com/mvntool/dependencies.json";
    private static final Path LOCAL_DB_PATH = Path.of(System.getProperty("user.home"), ".mvntool", "dependencies.json");
    private static Map<String, Map<String, Object>> database;

    public static DependencyInfo get(String artifact, String version) throws IOException {
        loadDatabase();

        Map<String, Object> dep = database.get(artifact);
        if (dep == null) {
            throw new RuntimeException("Artifact not found: " + artifact);
        }

        String resolvedVersion = version.equals("latest") ?
                dep.get("latest").toString() :
                ((Map<String, String>) dep.get("versions")).get(version);

        if (resolvedVersion == null) {
            throw new RuntimeException("Version not found: " + version);
        }

        return new DependencyInfo(
                dep.get("groupId").toString(),
                dep.get("artifactId").toString(),
                resolvedVersion
        );
    }

    private static synchronized void loadDatabase() throws IOException {
        if (database == null) {
            if (!Files.exists(LOCAL_DB_PATH)) {
                Files.createDirectories(LOCAL_DB_PATH.getParent());
                try (var in = new URL(DB_URL).openStream()) {
                    Files.copy(in, LOCAL_DB_PATH, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            database = new ObjectMapper().readValue(LOCAL_DB_PATH.toFile(), Map.class);
        }
    }
}
