package com.bmuschko.gradle.cargo.util

import com.bmuschko.gradle.cargo.util.fixture.ServletWarFixture

class InstallerUrlIntegrationSpec extends AbstractIntegrationSpec {

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
            
            cargo {
                deployable {
                    files = configurations.war
                    context = '$WAR_CONTEXT'
                }
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
}
