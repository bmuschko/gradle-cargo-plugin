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

import com.bmuschko.gradle.cargo.convention.Deployable
import com.bmuschko.gradle.cargo.tasks.AbstractCargoContainerTask
import com.bmuschko.gradle.cargo.tasks.daemon.CargoDaemon
import com.bmuschko.gradle.cargo.util.ProjectInfoHelper
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * <p>A {@link org.gradle.api.Plugin} that provides custom task types for deploying WAR/EAR files to local and remote web containers.
 * The plugin pre-configures the dependencies on the external Cargo libraries. You will still need to define at least
 * one repository that the plugin can use to look for the libraries.</p>
 */
class CargoBasePlugin implements Plugin<Project> {
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

        configureAbstractContainerTask(project)
    }

    private void configureAbstractContainerTask(Project project) {
        project.tasks.withType(AbstractCargoContainerTask) {
            conventionMapping.map('classpath') {
                def config = project.configurations[CONFIGURATION_NAME]

                if(config.dependencies.empty) {
                    project.dependencies {
                        cargo "org.codehaus.cargo:cargo-core-uberjar:$CARGO_DEFAULT_VERSION",
                              "org.codehaus.cargo:cargo-ant:$CARGO_DEFAULT_VERSION"
                    }
                }

                config
            }

            conventionMapping.map('deployables') { resolveDeployables(project) }
        }

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
    }

    private List<Deployable> resolveDeployables(Project project) {
        def deployables = []

        File projectDeployable = ProjectInfoHelper.getProjectDeployableFile(project)

        if(projectDeployable) {
            deployables << new Deployable(files: project.files(projectDeployable))
        }

        deployables
    }
}
