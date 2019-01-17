package com.bmuschko.gradle.cargo.util

import com.bmuschko.gradle.cargo.util.fixture.ServletWarFixture

class DeployableIntegrationSpec extends AbstractIntegrationSpec {

    ServletWarFixture servletWarFixture

    void setup() {
        servletWarFixture = new ServletWarFixture(testProjectDir.root, ":$WAR_CONTEXT")
        buildScript << """
            import com.bmuschko.gradle.cargo.tasks.local.LocalCargoContainerTask

            repositories {
                mavenCentral()
            }

            configurations {
                war
                tomcat
            }

            dependencies {
                war project(path: '${servletWarFixture.projectPath}', configuration: 'archives')
                
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

    void cleanup() {
        runBuild "cargoStopLocal"
    }

    def "can use a file as a deployable"() {
        given:
        buildScript << """
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

        when:
        runBuild "cargoStartLocal"

        then:
        requestServletResponseText() == ServletWarFixture.RESPONSE_TEXT
    }

    def "can use a file collection as a deployable"() {
        given:
        buildScript << """
            cargo {
                deployable {
                    file = configurations.war
                    context = '$WAR_CONTEXT'
                }
            }
        """

        when:
        runBuild "cargoStartLocal"

        then:
        requestServletResponseText() == ServletWarFixture.RESPONSE_TEXT
    }

}
