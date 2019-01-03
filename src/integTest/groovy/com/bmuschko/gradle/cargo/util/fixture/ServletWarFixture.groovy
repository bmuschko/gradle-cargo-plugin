package com.bmuschko.gradle.cargo.util.fixture

class ServletWarFixture {

    public final static String RESPONSE_TEXT = 'Hello World!'

    private final static String WEB_XML_FILENAME = 'web.xml'

    private final File rootProjectDir
    private final List<String> projectPathSegments

    ServletWarFixture(File rootProjectDir, String projectPath) {
        this.rootProjectDir = rootProjectDir
        this.projectPathSegments = projectPath.tokenize(":").findAll()
        configure()
    }

    String getProjectPath() {
        ":${projectPathSegments.join(":")}"
    }

    private File getProjectDir() {
        projectPathSegments ? new File(rootProjectDir, projectPathSegments.join('/')) : rootProjectDir
    }

    private void configure() {
        addProjectToSettings()
        projectDir.mkdirs()
        writeWebXml()
        writeBuild()
        writeSources()
    }

    private void addProjectToSettings() {
        new File(rootProjectDir, "settings.gradle") << """
            include '$projectPath' 
        """
    }

    private void writeWebXml() {
        new File(projectDir, WEB_XML_FILENAME) << '''\
            |<?xml version='1.0' encoding='utf-8'?>
            |<web-app>
            |   <servlet>
            |       <servlet-name>HelloWorld</servlet-name>
            |       <servlet-class>HelloWorld</servlet-class>
            |   </servlet>
            |   <servlet-mapping>
            |       <servlet-name>HelloWorld</servlet-name>
            |       <url-pattern>/</url-pattern>
            |   </servlet-mapping>
            |</web-app>
        '''.stripMargin()
    }

    private void writeBuild() {
        new File(projectDir, "build.gradle") << """
            apply plugin: 'war'
            
            repositories {
                mavenCentral()
            }

            dependencies {
                providedCompile "javax.servlet:javax.servlet-api:4.0.1"
            }

            war {
                webXml = file("$WEB_XML_FILENAME")
            }
        """
    }

    void writeSources() {
        def sourcesDirectory = new File(projectDir, 'src/main/java')
        sourcesDirectory.mkdirs()

        new File(sourcesDirectory, "HelloWorld.java") << """
            import javax.servlet.http.HttpServlet;
            import javax.servlet.http.HttpServletRequest;
            import javax.servlet.http.HttpServletResponse;
            import java.io.IOException;
            
            public class HelloWorld extends HttpServlet {
                @Override
                protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                    response.setContentType("text/plain");
                    response.getWriter().print("$RESPONSE_TEXT");
                }
            }
        """
    }
}
