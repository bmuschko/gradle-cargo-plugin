package com.bmuschko.gradle.cargo.util

import com.bmuschko.gradle.cargo.util.fixture.ServletWarFixture

class DeployableIntegrationSpec extends AbstractIntegrationSpec {

    ServletWarFixture servletWarFixture

    void setup() {
        servletWarFixture = new ServletWarFixture(testProjectDir.root, ":$WAR_CONTEXT")
        configureCargoInstaller()
        buildScript << """
            import com.bmuschko.gradle.cargo.tasks.local.LocalCargoContainerTask

            repositories {
                mavenCentral()
            }

            configurations {
                war
            }

            dependencies {
                war project(path: '${servletWarFixture.projectPath}', configuration: 'archives')
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
                    files = configurations.war
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
