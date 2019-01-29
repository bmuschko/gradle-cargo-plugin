package com.bmuschko.gradle.cargo.util

import com.bmuschko.gradle.cargo.util.fixture.TextResourceFactoryJarFixture
import com.bmuschko.gradle.cargo.util.fixture.TextResourceLoaderServletWarFixture
import groovyx.net.http.HttpBuilder

class ConfigFileIntegrationSpec extends AbstractIntegrationSpec {

    private final String TEXT_RESOURCE_NAME = "test/resource"
    private final String TEXT_RESOURCE_VALUE = "test resource value"

    void setup() {
        def textResourceFactoryJarFixture = new TextResourceFactoryJarFixture(testProjectDir.root, ":textResourceFactory")
        def textResourceLoaderServletWarFixture = new TextResourceLoaderServletWarFixture(testProjectDir.root, ":textResourceLoader")

        configureCargoInstaller()

        buildScript << """
            import groovy.xml.MarkupBuilder

            repositories {
                mavenCentral()
            }

            configurations {
                war
                extraClasspath
            }

            dependencies {
                war project(path: '${textResourceLoaderServletWarFixture.projectPath}', configuration: 'archives')
                extraClasspath project('$textResourceFactoryJarFixture.projectPath')
            }
            
            cargo {
                local {
                    extraClasspath = configurations.extraClasspath
                }
            
                deployable {
                    file = configurations.war
                    context = '$WAR_CONTEXT'
                }
            }
            
            task writeContextXml {
                def contextXml = new File(buildDir, "context.xml")
                
                outputs.file(contextXml)
                
                doLast {
                    contextXml.withWriter { writer ->
                        new MarkupBuilder(writer).Context {
                            Resource(
                                name: "$TEXT_RESOURCE_NAME",
                                factory: "TextResourceFactory",
                                value: "$TEXT_RESOURCE_VALUE"
                            )
                        }
                    }
                }
            }
        """
    }

    void cleanup() {
        runBuild "cargoStopLocal"
    }

    void "can use a file collection as a config files source"() {
        given:
        buildScript << """
            cargo {
                local {
                    configFile {
                        files = writeContextXml.outputs.files
                        toDir = "conf"
                    }
                }
            }
        """

        when:
        runBuild "cargoStartLocal"

        then:
        requestTextResourceValue(TEXT_RESOURCE_NAME) == TEXT_RESOURCE_VALUE
    }

    String requestTextResourceValue(String resourceName) {
        HttpBuilder.configure {
            request.uri = "http://localhost:8080/$WAR_CONTEXT"
            request.uri.query = [resourceName: resourceName]
        }.get()
    }

}
