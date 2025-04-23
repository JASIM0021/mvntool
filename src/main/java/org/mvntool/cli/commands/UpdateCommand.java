package org.mvntool.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Map;
import java.util.concurrent.Callable;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Command(
        name = "update",
        description = "Update the dependency database or the tool itself"
)
public class UpdateCommand implements Callable<Integer> {

    @Option(
            names = {"-t", "--tool"},
            description = "Update the mvntool CLI to the latest version"
    )
    private boolean updateTool;

    @Option(
            names = {"-f", "--force"},
            description = "Force update even if already up-to-date"
    )
    private boolean force;

    private static final String DB_URL = "https://yourdomain.com/mvntool/db/dependencies.json";
    private static final String TOOL_URL = "https://yourdomain.com/mvntool/latest/mvntool.jar";
    private static final Path DB_PATH = Path.of(System.getProperty("user.home"), ".mvntool", "dependencies.json");
    private static final Path TOOL_PATH = Path.of(System.getProperty("user.home"), ".mvntool", "mvntool.jar");

    @Override
    public Integer call() throws Exception {
        if (updateTool) {
            updateTool();
        } else {
            updateDatabase();
        }
        return 0;
    }

    private void updateDatabase() throws IOException {
        System.out.println("🔄 Checking for dependency updates...");

        // Get current version
        String currentVersion = "unknown";
        if (Files.exists(DB_PATH)) {
            Map<?, ?> currentDb = new ObjectMapper().readValue(DB_PATH.toFile(), Map.class);
            currentVersion = (String) currentDb.get("version");
        }

        // Download latest
        URL url = new URL(DB_URL);
        Files.createDirectories(DB_PATH.getParent());
        Files.copy(url.openStream(), DB_PATH, StandardCopyOption.REPLACE_EXISTING);

        // Verify update
        Map<?, ?> newDb = new ObjectMapper().readValue(DB_PATH.toFile(), Map.class);
        String newVersion = (String) newDb.get("version");

        if (!newVersion.equals(currentVersion) ){
            System.out.printf("✅ Updated dependency database to version %s%n", newVersion);
            System.out.printf("   Contains %d dependencies%n", ((Map<?, ?>) newDb.get("dependencies")).size());
        } else if (force) {
            System.out.println("✅ Forced database update (version unchanged)");
        } else {
            System.out.println("ℹ️ Dependency database is already up-to-date");
        }
    }

    private void updateTool() throws IOException {
        System.out.println("🔄 Checking for CLI updates...");

        // Get current version
        String currentVersion = getCurrentToolVersion();

        // Download latest
        URL url = new URL(TOOL_URL);
        Files.createDirectories(TOOL_PATH.getParent());
        Files.copy(url.openStream(), TOOL_PATH, StandardCopyOption.REPLACE_EXISTING);

        // Verify update
        String newVersion = getCurrentToolVersion();
        if (!newVersion.equals(currentVersion)) {
            System.out.printf("✅ Updated mvntool to version %s%n", newVersion);
            System.out.println("   Restart your terminal to use the new version");
        } else if (force) {
            System.out.println("✅ Forced tool update (version unchanged)");
        } else {
            System.out.println("ℹ️ mvntool is already up-to-date");
        }
    }

    private String getCurrentToolVersion() {
        try {
            Process process = new ProcessBuilder(TOOL_PATH.toString(), "--version")
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .start();
            String version = new String(process.getInputStream().readAllBytes()).trim();
            return version.split(" ")[1]; // Gets version from "mvntool 1.0.0"
        } catch (Exception e) {
            return "unknown";
        }
    }
}