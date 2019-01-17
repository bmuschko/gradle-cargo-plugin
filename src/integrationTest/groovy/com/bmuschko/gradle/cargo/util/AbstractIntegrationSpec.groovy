package com.bmuschko.gradle.cargo.util

import groovyx.net.http.HttpBuilder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class AbstractIntegrationSpec extends Specification {

    protected final static String WAR_CONTEXT = 'hello-world'
    @Rule
    TemporaryFolder testProjectDir

    File buildScript

    void setup() {
        buildScript = testProjectDir.newFile('build.gradle') << """
            plugins {
                id 'com.bmuschko.cargo'
            }
        """
    }

    BuildResult runBuild(String... arguments) {
        GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("-s", *arguments)
            .withPluginClasspath()
            .forwardOutput()
            .build()
    }

    String requestServletResponseText() {
        HttpBuilder.configure {
            request.uri = "http://localhost:8080/$WAR_CONTEXT"
        }.get()
    }
}
