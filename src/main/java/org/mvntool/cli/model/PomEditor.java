package org.mvntool.cli.model;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PomEditor {
    public static void addDependency(DependencyInfo info) throws IOException, XmlPullParserException {
        File pomFile = new File("pom.xml");
        if (!pomFile.exists()) {
            throw new RuntimeException("❌ pom.xml not found in current directory!");
        }

        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader(pomFile));

        // Initialize dependencies if null
        if (model.getDependencies() == null) {
            model.setDependencies(new java.util.ArrayList<>());
        }

        List<Dependency> dependencies = model.getDependencies();

        // Check for existing dependency
        Optional<Dependency> existingDep = dependencies.stream()
                .filter(dep -> dep.getGroupId().equals(info.getGroupId())
                        && dep.getArtifactId().equals(info.getArtifactId()))
                .findFirst();

        if (existingDep.isPresent()) {
            // Update existing dependency
            Dependency dep = existingDep.get();
            String oldVersion = dep.getVersion();
            dep.setVersion(info.getVersion());
            System.out.printf("🔄 Updated %s:%s from %s to %s%n",
                    info.getGroupId(), info.getArtifactId(), oldVersion, info.getVersion());
        } else {
            // Add new dependency
            Dependency newDep = new Dependency();
            newDep.setGroupId(info.getGroupId());
            newDep.setArtifactId(info.getArtifactId());
            newDep.setVersion(info.getVersion());
            dependencies.add(newDep);
            System.out.printf("✅ Added %s%n", info);
        }

        // Write changes back to pom.xml
        try (FileWriter writer = new FileWriter(pomFile)) {
            new MavenXpp3Writer().write(writer, model);
        }
    }
}