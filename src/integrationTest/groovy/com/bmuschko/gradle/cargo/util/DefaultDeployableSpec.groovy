package com.bmuschko.gradle.cargo.util

import com.bmuschko.gradle.cargo.util.fixture.ServletWarFixture

class DefaultDeployableSpec extends AbstractIntegrationSpec {

    ServletWarFixture servletWarFixture

    void setup() {
        servletWarFixture = new ServletWarFixture(testProjectDir.root, ":")
    }

    void cleanup() {
        runBuild "cargoStopLocal"
    }

    def "project artifact is configured as a deployable including task dependencies"() {
        given:
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

                deployable {
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
