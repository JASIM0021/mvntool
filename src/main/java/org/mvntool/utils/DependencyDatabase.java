package org.mvntool.utils;

import org.apache.maven.model.Dependency;

import java.util.Map;

public class DependencyDatabase {
    private String version;
    private Map<String, Dependency> dependencies;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Map<String, Dependency> dependencies) {
        this.dependencies = dependencies;
    }
}

