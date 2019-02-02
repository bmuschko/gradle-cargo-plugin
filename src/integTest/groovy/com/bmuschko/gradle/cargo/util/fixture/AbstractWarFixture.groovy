package com.bmuschko.gradle.cargo.util.fixture

import groovy.transform.InheritConstructors

@InheritConstructors
abstract class AbstractWarFixture extends ProjectFixture {

    private final static String WEB_XML_FILENAME = 'web.xml'

    abstract String getServletClassName()
    abstract String getServletClassSource()

    protected void configure() {
        super.configure()
        writeWebXml()
        writeBuild()
        writeSources()
    }

    protected void writeWebXml() {
        new File(projectDir, WEB_XML_FILENAME) << """\
            |<?xml version='1.0' encoding='utf-8'?>
            |<web-app>
            |   <servlet>
            |       <servlet-name>$servletClassName</servlet-name>
            |       <servlet-class>$servletClassName</servlet-class>
            |   </servlet>
            |   <servlet-mapping>
            |       <servlet-name>$servletClassName</servlet-name>
            |       <url-pattern>/</url-pattern>
            |   </servlet-mapping>
            |</web-app>
        """.stripMargin()
    }

    protected void writeBuild() {
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

    protected void writeSources() {
        def sourcesDirectory = new File(projectDir, 'src/main/java')
        sourcesDirectory.mkdirs()

        new File(sourcesDirectory, "${servletClassName}.java") << servletClassSource
    }
}
