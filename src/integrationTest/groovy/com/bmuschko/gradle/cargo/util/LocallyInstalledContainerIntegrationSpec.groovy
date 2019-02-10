package com.bmuschko.gradle.cargo.util

import com.bmuschko.gradle.cargo.util.fixture.HelloWorldServletWarFixture

class LocallyInstalledContainerIntegrationSpec extends AbstractIntegrationSpec{

    HelloWorldServletWarFixture servletWarFixture

    def setup() {
        servletWarFixture = new HelloWorldServletWarFixture(testProjectDir.root, ":$WAR_CONTEXT")
    }

    void cleanup() {
        runBuild "cargoStopLocal"
    }

    def "can use a locally installed container"() {
        buildScript << """
            repositories {
                mavenCentral()
            }
            
            configurations {
                tomcat
                war
            }

            dependencies {
                tomcat "org.apache.tomcat:tomcat:9.0.14@zip"
                war project(path: '${servletWarFixture.projectPath}', configuration: 'archives')
            }
            
            task installTomcat(type: Copy) {
                from { zipTree(configurations.tomcat.singleFile) }
                into "\$buildDir/tomcat-home"
                eachFile { FileCopyDetails fileCopyDetails ->
                    def original = fileCopyDetails.relativePath
                    //strip the top level directory
                    fileCopyDetails.relativePath = new RelativePath(original.file, *original.segments[1..-1])
                }
            }
            
            cargo {
                containerId = "tomcat9x"
                
                deployable {
                    file = configurations.war
                    context = '$WAR_CONTEXT'
                }
                
                local {
                    homeDir = installTomcat.outputs.files.singleFile
                }
            }
            
            cargoStartLocal.dependsOn installTomcat
        """

        when:
        runBuild "cargoStartLocal"

        then:
        requestServletResponseText() == HelloWorldServletWarFixture.RESPONSE_TEXT
    }
}
