package com.bmuschko.gradle.cargo.util.fixture

class ProjectFixture {

    protected final File rootProjectDir
    protected final List<String> projectPathSegments

    ProjectFixture(File rootProjectDir, String projectPath) {
        this.rootProjectDir = rootProjectDir
        this.projectPathSegments = projectPath.tokenize(":").findAll()
        configure()
    }

    protected void configure() {
        addProjectToSettings()
        projectDir.mkdirs()
    }

    protected void addProjectToSettings() {
        new File(rootProjectDir, "settings.gradle") << """
            include '$projectPath' 
        """
    }

    protected File getProjectDir() {
        projectPathSegments ? new File(rootProjectDir, projectPathSegments.join('/')) : rootProjectDir
    }

    String getProjectPath() {
        ":${projectPathSegments.join(":")}"
    }


}
