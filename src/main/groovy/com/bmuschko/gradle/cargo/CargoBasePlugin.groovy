/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bmuschko.gradle.cargo

import com.bmuschko.gradle.cargo.convention.CargoPluginExtension
import com.bmuschko.gradle.cargo.convention.Deployable
import com.bmuschko.gradle.cargo.tasks.AbstractCargoContainerTask
import com.bmuschko.gradle.cargo.tasks.daemon.CargoDaemon
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.WarPlugin
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.bundling.War
import org.gradle.plugins.ear.Ear
import org.gradle.plugins.ear.EarPlugin

/**
 * <p>A {@link org.gradle.api.Plugin} that provides custom task types for deploying WAR/EAR files to local and remote web containers.
 * The plugin pre-configures the dependencies on the external Cargo libraries. You will still need to define at least
 * one repository that the plugin can use to look for the libraries.</p>
 */
class CargoBasePlugin implements Plugin<Project> {
    static final String EXTENSION_NAME = 'cargo'
    static final String CONFIGURATION_NAME = 'cargo'
    static final String DAEMON_CONFIGURATION_NAME = 'cargoDaemon'
    static final String CARGO_DEFAULT_VERSION = '1.6.8'

    @Override
    void apply(Project project) {
        project.configurations.create(CONFIGURATION_NAME)
               .setVisible(false)
               .setTransitive(true)
               .setDescription('The Cargo Ant libraries to be used for this project.')


        project.configurations.create(DAEMON_CONFIGURATION_NAME)
                .setVisible(false)
                .setTransitive(true)
                .setDescription('The Cargo daemon client libraries to be used for this project.')

        def extension = project.extensions.create(EXTENSION_NAME, CargoPluginExtension, project)

        configureAbstractContainerTask(project, extension)
        configureEarPlugin(project, extension)
        configureWarPlugin(project, extension)
                    }

    private void configureAbstractContainerTask(Project project, CargoPluginExtension cargoPluginExtension) {
        def alternativeConfiguration = project.configurations.detachedConfiguration(project.dependencies.create("org.codehaus.cargo:cargo-core-uberjar:$CARGO_DEFAULT_VERSION"),
                project.dependencies.create("org.codehaus.cargo:cargo-ant:$CARGO_DEFAULT_VERSION"))

        project.tasks.withType(AbstractCargoContainerTask, { AbstractCargoContainerTask task ->
            task.classpath.setFrom(project.configurations[CONFIGURATION_NAME])
            task.alternativeClasspath.setFrom(alternativeConfiguration)
        })

        project.tasks.withType(CargoDaemon) {
            conventionMapping.map('classpath') {
                def config = project.configurations[DAEMON_CONFIGURATION_NAME]

                if(config.dependencies.empty) {
                    project.dependencies {
                        cargoDaemon "org.codehaus.cargo:cargo-daemon-client:$CARGO_DEFAULT_VERSION"
                    }
                }

                config
            }
        }

        project.tasks.withType(AbstractCargoContainerTask, { AbstractCargoContainerTask task ->
            task.containerId.set(cargoPluginExtension.containerId)
            task.port.set(cargoPluginExtension.port)
            task.deployables.convention(cargoPluginExtension.deployables)
            task.timeout.set(cargoPluginExtension.timeout)
        })
    }

    /**
     * Add deployable for War if war plugin is applied
     */
    private void configureWarPlugin(Project project, CargoPluginExtension extension) {
        project.plugins.withType(WarPlugin, {
            WarPlugin warPlugin ->
                project.tasks.withType(War, {
                    AbstractArchiveTask war -> extension.deployables.add(createDeployableFromAbstract(project, war))
                })
        })
    }

    /**
     * Add deployable for Ear if war plugin is applied
     */
    private void configureEarPlugin(Project project, CargoPluginExtension extension) {
        project.plugins.withType(EarPlugin, {
            EarPlugin warPlugin ->
                project.tasks.withType(Ear, {
                    AbstractArchiveTask ear -> extension.deployables.add(createDeployableFromAbstract(project, ear))
                })
        })
    }

    // Create a deployable and set file from archive task
    private Deployable createDeployableFromAbstract(Project project, AbstractArchiveTask archiveTask) {

        def deployable = new Deployable(project)
        deployable.setFile(archiveTask.getArchiveFile().map({ RegularFile f -> f.getAsFile()}))
        return deployable
    }
}
