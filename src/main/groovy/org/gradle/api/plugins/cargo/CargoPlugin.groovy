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
import org.gradle.api.plugins.WarPlugin
import org.gradle.api.plugins.cargo.convention.CargoPluginConvention
import org.gradle.api.plugins.cargo.convention.Deployable
import org.gradle.api.plugins.cargo.property.AbstractContainerTaskProperty
import org.gradle.api.plugins.cargo.property.CargoProjectProperty
import org.gradle.api.plugins.cargo.property.LocalContainerTaskProperty
import org.gradle.api.plugins.cargo.property.RemoteContainerTaskProperty
import org.gradle.plugins.ear.EarPlugin

/**
 * <p>A {@link org.gradle.api.Plugin} that provides tasks for deploying WAR/EAR files to local and remote web containers.</p>
 *
 * @author Benjamin Muschko
 */
class CargoPlugin implements Plugin<Project> {
    static final String CARGO_CONFIGURATION_NAME = 'cargo'
    static final String CARGO_TASK_GROUP = 'deployment'

    @Override
    void apply(Project project) {
        project.configurations.create(CARGO_CONFIGURATION_NAME)
                .setVisible(false)
                .setTransitive(true)
                .setDescription('The Cargo Ant libraries to be used for this project.')

        CargoPluginConvention cargoConvention = new CargoPluginConvention()
        project.convention.plugins.cargo = cargoConvention

        configureAbstractContainerTask(project, cargoConvention)
        configureRemoteContainerTasks(project, cargoConvention)
        configureLocalContainerTasks(project, cargoConvention)
        checkValidContainerId(project, cargoConvention)
    }

    private void configureAbstractContainerTask(Project project, CargoPluginConvention cargoConvention) {
        project.tasks.withType(AbstractContainerTask) {
            conventionMapping.map('classpath') { project.configurations.getByName(CARGO_CONFIGURATION_NAME).asFileTree }
            conventionMapping.map('containerId') {
                CargoProjectProperty.getTypedProperty(project, AbstractContainerTaskProperty.CONTAINER_ID, cargoConvention.containerId)
            }
            conventionMapping.map('port') {
                CargoProjectProperty.getTypedProperty(project, AbstractContainerTaskProperty.PORT, cargoConvention.port)
            }
            conventionMapping.map('deployables') { resolveDeployables(project, cargoConvention) }
        }
    }

    private void configureLocalContainerConventionMapping(Project project, CargoPluginConvention cargoConvention) {
        project.tasks.withType(LocalContainerTask) {
            conventionMapping.map('jvmArgs') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.JVM_ARGS, cargoConvention.local.jvmArgs)
            }
            conventionMapping.map('logLevel') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.LOG_LEVEL, cargoConvention.local.logLevel)
            }
            conventionMapping.map('homeDir') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.HOME_DIR, cargoConvention.local.homeDir)
            }
            conventionMapping.map('configHomeDir') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.CONFIG_HOME_DIR, cargoConvention.local.configHomeDir)
            }
            conventionMapping.map('output') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.OUTPUT, cargoConvention.local.output)
            }
            conventionMapping.map('logFile') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.LOG, cargoConvention.local.log)
            }
            conventionMapping.map('rmiPort') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.RMI_PORT, cargoConvention.local.rmiPort)
            }
            conventionMapping.map('timeout') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.TIMEOUT, cargoConvention.timeout)
            }
            conventionMapping.map('zipUrlInstaller') { cargoConvention.local.zipUrlInstaller }
            conventionMapping.map('configFiles') { cargoConvention.local.configFiles }
            conventionMapping.map('files') { cargoConvention.local.files }
            conventionMapping.map('containerProperties') { cargoConvention.local.containerProperties.properties }
            conventionMapping.map('systemProperties') { cargoConvention.local.systemProperties.properties }

        }
    }

    private void setRemoteContainerConventionMapping(Project project, CargoPluginConvention cargoConvention) {
        project.tasks.withType(RemoteContainerTask) {
            conventionMapping.map('protocol') {
                CargoProjectProperty.getTypedProperty(project, RemoteContainerTaskProperty.PROTOCOL, cargoConvention.remote.protocol)
            }
            conventionMapping.map('hostname') {
                CargoProjectProperty.getTypedProperty(project, RemoteContainerTaskProperty.HOSTNAME, cargoConvention.remote.hostname)
            }
            conventionMapping.map('username') {
                CargoProjectProperty.getTypedProperty(project, RemoteContainerTaskProperty.USERNAME, cargoConvention.remote.username)
            }
            conventionMapping.map('password') {
                CargoProjectProperty.getTypedProperty(project, RemoteContainerTaskProperty.PASSWORD, cargoConvention.remote.password)
            }
            conventionMapping.map('containerProperties') { cargoConvention.remote.containerProperties.properties }
        }
    }

    private void configureRemoteContainerTasks(Project project, CargoPluginConvention cargoConvention) {
        setRemoteContainerConventionMapping(project, cargoConvention)
        addContainerTask(project, CargoPluginTask.DEPLOY_REMOTE)
        addContainerTask(project, CargoPluginTask.UNDEPLOY_REMOTE)
        addContainerTask(project, CargoPluginTask.REDEPLOY_REMOTE)
    }

    private void configureLocalContainerTasks(Project project, CargoPluginConvention cargoConvention) {
        configureLocalContainerConventionMapping(project, cargoConvention)
        addContainerTask(project, CargoPluginTask.RUN_LOCAL)
        addContainerTask(project, CargoPluginTask.START_LOCAL)
        addContainerTask(project, CargoPluginTask.STOP_LOCAL)
    }

    private void checkValidContainerId(Project project, CargoPluginConvention cargoConvention) {
        project.gradle.taskGraph.whenReady { TaskExecutionGraph taskGraph ->
            if(containsCargoTask(taskGraph)) {
                if(!cargoConvention.containerId) {
                    throw new InvalidUserDataException('Container ID was not defined.')
                }
            }
        }
    }

    private boolean containsCargoTask(TaskExecutionGraph taskGraph) {
        taskGraph.allTasks.findAll { task -> task instanceof AbstractContainerTask }.size() > 0
    }

    private void addContainerTask(Project project, CargoPluginTask task) {
        project.task(task.name, type: task.taskClass) {
            description = task.description
            group = CARGO_TASK_GROUP
            conventionMapping.map('action') { task.action.name }
        }
    }

    private List<Deployable> resolveDeployables(Project project, CargoPluginConvention cargoConvention) {
        def deployables = []

        if(cargoConvention.deployables.size() == 0) {
            deployables << new Deployable(file: getProjectDeployableFile(project))
        }
        else {
            cargoConvention.deployables.each { deployable ->
                if(!deployable.file) {
                    deployable.file = getProjectDeployableFile(project)
                }

                deployables << deployable
            }
        }

        deployables
    }

    private File getProjectDeployableFile(Project project) {
        if(project.plugins.hasPlugin(WarPlugin.WAR_TASK_NAME)) {
            return project.tasks.getByName(WarPlugin.WAR_TASK_NAME).archivePath
        }
        else if(project.plugins.hasPlugin(EarPlugin.EAR_TASK_NAME)) {
            return project.tasks.getByName(EarPlugin.EAR_TASK_NAME).archivePath
        }
    }
}
