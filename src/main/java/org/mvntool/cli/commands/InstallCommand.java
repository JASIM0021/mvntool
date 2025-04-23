package org.mvntool.cli.commands;


import org.mvntool.cli.model.DependencyInfo;
import org.mvntool.cli.model.PomEditor;
import org.mvntool.utils.DependencyDB;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(
        name = "install",
        description = "Install a Maven dependency"
)
public class InstallCommand implements Callable<Integer> {
    @Parameters(paramLabel = "<dependency>", description = "Dependency (e.g., opencsv@5.1.0)")
    private String dependency;

    @Override
    public Integer call() throws Exception {
        String[] parts = dependency.split("@");
        String artifact = parts[0];
        String version = (parts.length > 1) ? parts[1] : "latest";

        DependencyInfo info = DependencyDB.get(artifact, version);
        PomEditor.addDependency(info);

        System.out.printf("✅ Added %s:%s:%s to pom.xml%n",
                info.getGroupId(), info.getArtifactId(), info.getVersion());
        return 0;
    }
}