/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.api.plugins.cargo

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.plugins.cargo.convention.CargoPluginExtension
import org.gradle.api.plugins.cargo.convention.Deployable
import org.gradle.api.plugins.cargo.property.AbstractContainerTaskProperty
import org.gradle.api.plugins.cargo.property.CargoProjectProperty
import org.gradle.api.plugins.cargo.property.LocalContainerTaskProperty
import org.gradle.api.plugins.cargo.property.RemoteContainerTaskProperty
import org.gradle.api.plugins.cargo.tasks.AbstractCargoContainerTask
import org.gradle.api.plugins.cargo.tasks.local.CargoRunLocal
import org.gradle.api.plugins.cargo.tasks.local.CargoStartLocal
import org.gradle.api.plugins.cargo.tasks.local.CargoStopLocal
import org.gradle.api.plugins.cargo.tasks.local.LocalCargoContainerTask
import org.gradle.api.plugins.cargo.tasks.remote.CargoDeployRemote
import org.gradle.api.plugins.cargo.tasks.remote.CargoRedeployRemote
import org.gradle.api.plugins.cargo.tasks.remote.CargoUndeployRemote
import org.gradle.api.plugins.cargo.tasks.remote.RemoteCargoContainerTask
import org.gradle.api.plugins.cargo.util.ProjectInfoHelper

/**
 * <p>A {@link org.gradle.api.Plugin} that provides pre-configured tasks for deploying WAR/EAR files to local and remote web containers.</p>
 *
 * @author Benjamin Muschko
 */
class CargoPlugin implements Plugin<Project> {
    static final String EXTENSION_NAME = 'cargo'

    @Override
    void apply(Project project) {
        project.plugins.apply(CargoBasePlugin)

        CargoPluginExtension cargoPluginExtension = project.extensions.create(EXTENSION_NAME, CargoPluginExtension)

        configureAbstractContainerTask(project, cargoPluginExtension)
        configureRemoteContainerTasks(project, cargoPluginExtension)
        configureLocalContainerTasks(project, cargoPluginExtension)
        checkValidContainerId(project, cargoPluginExtension)
    }

    private void configureAbstractContainerTask(Project project, CargoPluginExtension cargoPluginExtension) {
        project.tasks.withType(AbstractCargoContainerTask) {
            conventionMapping.map('containerId') {
                CargoProjectProperty.getTypedProperty(project, AbstractContainerTaskProperty.CONTAINER_ID, cargoPluginExtension.containerId)
            }
            conventionMapping.map('port') {
                CargoProjectProperty.getTypedProperty(project, AbstractContainerTaskProperty.PORT, cargoPluginExtension.port)
            }
            conventionMapping.map('deployables') { resolveDeployables(project, cargoPluginExtension) }
        }
    }

    private void configureLocalContainerConventionMapping(Project project, CargoPluginExtension cargoPluginExtension) {
        project.tasks.withType(LocalCargoContainerTask) {
            conventionMapping.map('jvmArgs') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.JVM_ARGS, cargoPluginExtension.local.jvmArgs)
            }
            conventionMapping.map('logLevel') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.LOG_LEVEL, cargoPluginExtension.local.logLevel)
            }
            conventionMapping.map('homeDir') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.HOME_DIR, cargoPluginExtension.local.homeDir)
            }
            conventionMapping.map('configHomeDir') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.CONFIG_HOME_DIR, cargoPluginExtension.local.configHomeDir)
            }
            conventionMapping.map('outputFile') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.OUTPUT, cargoPluginExtension.local.outputFile)
            }
            conventionMapping.map('logFile') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.LOG, cargoPluginExtension.local.log)
            }
            conventionMapping.map('rmiPort') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.RMI_PORT, cargoPluginExtension.local.rmiPort)
            }
            conventionMapping.map('timeout') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.TIMEOUT, cargoPluginExtension.timeout)
            }
            conventionMapping.map('zipUrlInstaller') { cargoPluginExtension.local.zipUrlInstaller }
            conventionMapping.map('configFiles') { cargoPluginExtension.local.configFiles }
            conventionMapping.map('files') { cargoPluginExtension.local.files }
            conventionMapping.map('containerProperties') { cargoPluginExtension.local.containerProperties.properties }
            conventionMapping.map('systemProperties') { cargoPluginExtension.local.systemProperties.properties }
            conventionMapping.map('extraClasspath') { cargoPluginExtension.local.extraClasspath }
            conventionMapping.map('sharedClasspath') { cargoPluginExtension.local.sharedClasspath }
        }
    }

    private void setRemoteContainerConventionMapping(Project project, CargoPluginExtension cargoPluginExtension) {
        project.tasks.withType(RemoteCargoContainerTask) {
            conventionMapping.map('protocol') {
                CargoProjectProperty.getTypedProperty(project, RemoteContainerTaskProperty.PROTOCOL, cargoPluginExtension.remote.protocol)
            }
            conventionMapping.map('hostname') {
                CargoProjectProperty.getTypedProperty(project, RemoteContainerTaskProperty.HOSTNAME, cargoPluginExtension.remote.hostname)
            }
            conventionMapping.map('username') {
                CargoProjectProperty.getTypedProperty(project, RemoteContainerTaskProperty.USERNAME, cargoPluginExtension.remote.username)
            }
            conventionMapping.map('password') {
                CargoProjectProperty.getTypedProperty(project, RemoteContainerTaskProperty.PASSWORD, cargoPluginExtension.remote.password)
            }
            conventionMapping.map('containerProperties') { cargoPluginExtension.remote.containerProperties.properties }
        }
    }

    private void configureRemoteContainerTasks(Project project, CargoPluginExtension cargoPluginExtension) {
        setRemoteContainerConventionMapping(project, cargoPluginExtension)
        project.task('cargoDeployRemote', type: CargoDeployRemote)
        project.task('cargoUndeployRemote', type: CargoUndeployRemote)
        project.task('cargoRedeployRemote', type: CargoRedeployRemote)
    }

    private void configureLocalContainerTasks(Project project, CargoPluginExtension cargoPluginExtension) {
        configureLocalContainerConventionMapping(project, cargoPluginExtension)
        project.task('cargoRunLocal', type: CargoRunLocal)
        project.task('cargoStartLocal', type: CargoStartLocal)
        project.task('cargoStopLocal', type: CargoStopLocal)
    }

    private void checkValidContainerId(Project project, CargoPluginExtension cargoPluginExtension) {
        project.gradle.taskGraph.whenReady { TaskExecutionGraph taskGraph ->
            if(containsCargoTask(taskGraph)) {
                if(!cargoPluginExtension.containerId) {
                    throw new InvalidUserDataException('Container ID was not defined.')
                }
            }
        }
    }

    private boolean containsCargoTask(TaskExecutionGraph taskGraph) {
        taskGraph.allTasks.findAll { task -> task instanceof AbstractCargoContainerTask }.size() > 0
    }

    private List<Deployable> resolveDeployables(Project project, CargoPluginExtension cargoConvention) {
        def deployables = []

        if(cargoConvention.deployables.size() == 0) {
            deployables << new Deployable(file: ProjectInfoHelper.getProjectDeployableFile(project))
        }
        else {
            cargoConvention.deployables.each { deployable ->
                if(!deployable.file) {
                    deployable.file = ProjectInfoHelper.getProjectDeployableFile(project)
                }

                deployables << deployable
            }
        }

        deployables
    }
}
