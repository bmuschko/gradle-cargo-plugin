package com.bmuschko.gradle.cargo.util

import com.bmuschko.gradle.cargo.util.fixture.ServletWarFixture
import groovyx.net.http.HttpBuilder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class InstallerUrlIntegrationSpec extends Specification {

    final static String WAR_CONTEXT = 'hello-world'

    @Rule
    TemporaryFolder testProjectDir

    ServletWarFixture servletWarFixture
    File buildScript

    void setup() {
        servletWarFixture = new ServletWarFixture(testProjectDir.root, ":$WAR_CONTEXT")
        buildScript = testProjectDir.newFile('build.gradle') << """
            import com.bmuschko.gradle.cargo.tasks.local.LocalCargoContainerTask

            plugins {
                id 'com.bmuschko.cargo'
            }
            
            repositories {
                mavenCentral()
            }

            configurations {
                war
            }

            dependencies {
                war project(path: '${servletWarFixture.projectPath}', configuration: 'archives')
            }

            cargo {
                containerId = "tomcat9x"
                
                local {
                    installer {
                        downloadDir = file("\$buildDir/download")
                        extractDir = file("\$buildDir/extract")
                    }
                }
            }
            
            task configureCargoDeployable {
                inputs.files(configurations.war)
                
                doLast {
                    cargo {
                        deployable {
                            file = configurations.war.singleFile
                            context = '$WAR_CONTEXT'
                        }
                    }
                }
            }
            
            tasks.withType(LocalCargoContainerTask) {
                dependsOn configureCargoDeployable
            }
        """
    }

    void cleanup() {
        runBuild "cargoStopLocal"
    }

    void "url can be used to configure installer source"() {
        given:
        buildScript << """
            cargo {
                local {
                    installer {
                        installUrl = "https://repo1.maven.org/maven2/org/apache/tomcat/tomcat/9.0.14/tomcat-9.0.14.zip"
                    }
                }
            }
        """
        when:
        runBuild "cargoStartLocal"

        then:
        requestServletResponseText() == ServletWarFixture.RESPONSE_TEXT
    }

    void "configuration can be used to configure installer source"() {
        given:
        buildScript << """
            configurations {
                tomcat
            }
            
            dependencies {
                tomcat "org.apache.tomcat:tomcat:9.0.14@zip"
            }
            
            cargo {
                local {
                    installer {
                        installConfiguration = configurations.tomcat
                    }
                }
            }
        """
        when:
        runBuild "cargoStartLocal"

        then:
        requestServletResponseText() == ServletWarFixture.RESPONSE_TEXT
    }

    private BuildResult runBuild(String... arguments) {
        GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("-s", *arguments)
            .withPluginClasspath()
            .forwardOutput()
            .build()
    }

    private String requestServletResponseText() {
        HttpBuilder.configure {
            request.uri = "http://localhost:8080/$WAR_CONTEXT"
        }.get()
    }
}
