package com.bmuschko.gradle.cargo.util

import com.bmuschko.gradle.cargo.util.fixture.ServletWarFixture

class DefaultDeployableSpec extends AbstractIntegrationSpec {

    ServletWarFixture servletWarFixture

    void setup() {
        servletWarFixture = new ServletWarFixture(testProjectDir.root, ":")
        configureCargoInstaller()
    }

    void cleanup() {
        runBuild "cargoStopLocal"
    }

    def "project artifact is configured as a deployable including task dependencies"() {
        given:
        buildScript << """
            cargo {
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
