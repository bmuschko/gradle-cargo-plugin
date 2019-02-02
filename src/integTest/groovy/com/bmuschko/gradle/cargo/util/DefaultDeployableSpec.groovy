package com.bmuschko.gradle.cargo.util

import com.bmuschko.gradle.cargo.util.fixture.HelloWorldServletWarFixture

class DefaultDeployableSpec extends AbstractIntegrationSpec {

    HelloWorldServletWarFixture servletWarFixture

    void setup() {
        servletWarFixture = new HelloWorldServletWarFixture(testProjectDir.root, ":")
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
        requestServletResponseText() == HelloWorldServletWarFixture.RESPONSE_TEXT
    }


}
