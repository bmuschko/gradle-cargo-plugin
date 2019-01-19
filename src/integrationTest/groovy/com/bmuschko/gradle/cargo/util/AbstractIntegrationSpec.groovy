package com.bmuschko.gradle.cargo.util

import groovyx.net.http.HttpBuilder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class AbstractIntegrationSpec extends Specification {

    protected final static String WAR_CONTEXT = 'test-servlet'

    @Rule
    TemporaryFolder testProjectDir

    File buildScript

    void setup() {
        buildScript = testProjectDir.newFile('build.gradle') << """
            plugins {
                id 'com.bmuschko.cargo'
            }
        """
    }

    void configureCargoInstaller() {
        buildScript << """
            configurations {
                tomcat
            }

            dependencies {
                tomcat "org.apache.tomcat:tomcat:9.0.14@zip"
            }

            cargo {
                containerId = "tomcat9x"
                
                local {
                    installer {
                        installConfiguration = configurations.tomcat
                        downloadDir = file("\$buildDir/download")
                        extractDir = file("\$buildDir/extract")
                    }
                }
            }
        """
    }

    BuildResult runBuild(String... arguments) {
        GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("-s", *arguments)
            .withPluginClasspath()
            .forwardOutput()
            .build()
    }

    String requestServletResponseText() {
        HttpBuilder.configure {
            request.uri = "http://localhost:8080/$WAR_CONTEXT"
        }.get()
    }
}
