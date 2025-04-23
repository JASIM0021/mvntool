package org.mvntool.cli.model;

/**
 * Represents a Maven dependency with groupId, artifactId, and version.
 */
public class DependencyInfo {
    private final String groupId;
    private final String artifactId;
    private final String version;

    public DependencyInfo(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    // Getters
    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return String.format("%s:%s:%s", groupId, artifactId, version);
    }
}