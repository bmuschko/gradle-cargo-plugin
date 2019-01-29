package com.bmuschko.gradle.cargo.util

import com.bmuschko.gradle.cargo.util.fixture.HelloWorldServletWarFixture

class DeployableIntegrationSpec extends AbstractIntegrationSpec {

    HelloWorldServletWarFixture servletWarFixture

    void setup() {
        servletWarFixture = new HelloWorldServletWarFixture(testProjectDir.root, ":$WAR_CONTEXT")
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
        requestServletResponseText() == HelloWorldServletWarFixture.RESPONSE_TEXT
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
        requestServletResponseText() == HelloWorldServletWarFixture.RESPONSE_TEXT
    }

}
