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

public class PomEditor {
    public static void addDependency(DependencyInfo info) throws IOException, XmlPullParserException {
        File pomFile = new File("pom.xml");
        if (!pomFile.exists()) {
            throw new RuntimeException("❌ pom.xml not found in current directory!");
        }

        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader(pomFile));

        Dependency newDep = new Dependency();
        newDep.setGroupId(info.getGroupId());
        newDep.setArtifactId(info.getArtifactId());
        newDep.setVersion(info.getVersion());

        model.addDependency(newDep);

        MavenXpp3Writer writer = new MavenXpp3Writer();
        writer.write(new FileWriter(pomFile), model);
    }
}
