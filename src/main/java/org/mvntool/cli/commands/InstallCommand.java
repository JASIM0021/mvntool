package org.mvntool.cli.commands;

import org.mvntool.cli.model.DependencyInfo;
import org.mvntool.cli.model.PomEditor;
import org.mvntool.utils.DependencyDB;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(
        name = "install",
        description = "Install a Maven dependency and run 'mvn install'"
)
public class InstallCommand implements Callable<Integer> {

    @Parameters(
            paramLabel = "<dependency>",
            description = "Dependency in format: artifactId[@version] (e.g., opencsv@5.1.0)",
            arity = "1"
    )
    private String dependency;

    @Override
    public Integer call() {
        try {
            // Parse input
            String[] parts = dependency.split("@");
            String artifact = parts[0];
            String version = (parts.length > 1) ? parts[1] : "latest";

            // Get dependency info
            DependencyInfo info = DependencyDB.get(artifact, version);

            // Modify POM
            PomEditor.addDependency(info);
            System.out.printf("📦 Added/updated dependency: %s%n", info);

            // Run Maven install
            System.out.println("🛠️ Running 'mvn install'...");
            int exitCode = runMavenInstall();

            if (exitCode == 0) {
                System.out.println("✅ Successfully installed dependencies");
                return 0;
            } else {
                System.err.println("❌ Maven install failed");
                return exitCode;
            }

        } catch (IllegalArgumentException e) {
            System.err.println("❌ Invalid dependency format. Use: artifactId[@version]");
            return 1;
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            return 1;
        }
    }

    private int runMavenInstall() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("mvn", "clean", "install");
        pb.inheritIO(); // Forward output to console

        Process process = pb.start();
        return process.waitFor();
    }
}