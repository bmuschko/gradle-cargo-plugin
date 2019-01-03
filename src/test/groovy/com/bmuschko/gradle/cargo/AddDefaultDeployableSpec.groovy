package com.bmuschko.gradle.cargo

import com.bmuschko.gradle.cargo.convention.CargoPluginExtension
import org.gradle.api.Project
import org.gradle.api.plugins.WarPlugin
import org.gradle.api.tasks.bundling.War
import org.gradle.plugins.ear.Ear
import org.gradle.plugins.ear.EarPlugin
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Test default setup for cargo plugin if either EarPlugin or WarPlugin is applied.
 */
class AddDefaultDeployableSpec extends Specification {
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    def "no war or ear plugin will not add a default deployable"() {
        when:
        project.plugins.apply(CargoBasePlugin)

        then:
        project.getExtensions().getByType(CargoPluginExtension).deployables.get().size() == 0
    }


    def "war's path is added to deployable"() {
        when:
        project.plugins.apply(WarPlugin)
        project.plugins.apply(CargoBasePlugin)

        then:
        project.getExtensions().getByType(CargoPluginExtension).deployables.get().size() == 1
        project.getExtensions().getByType(CargoPluginExtension).deployables.get().get(0).getFile().get().canonicalPath == project.tasks.withType(War).first().getArchiveFile().get().getAsFile().canonicalPath
    }

    def "ear's path is added to deployable"() {
        when:
        project.plugins.apply(EarPlugin)
        project.plugins.apply(CargoBasePlugin)

        then:
        project.getExtensions().getByType(CargoPluginExtension).deployables.get().size() == 1
        project.getExtensions().getByType(CargoPluginExtension).deployables.get().get(0).getFile().get().canonicalPath == project.tasks.withType(Ear).first().getArchiveFile().get().getAsFile().canonicalPath
    }
}
