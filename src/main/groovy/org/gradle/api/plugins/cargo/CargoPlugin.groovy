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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.WarPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * <p>A {@link org.gradle.api.Plugin} that provides tasks for deploying WAR files to local and remote web containers.</p>
 *
 * @author Benjamin Muschko
 */
class CargoPlugin implements Plugin<Project> {
    static final Logger LOGGER = LoggerFactory.getLogger(CargoPlugin.class)
    static final String DEPLOY_REMOTE = 'cargoDeployRemote'
    static final String UNDEPLOY_REMOTE = 'cargoUndeployRemote'
    static final String REDEPLOY_REMOTE = 'cargoRedeployRemote'
    static final String START_LOCAL = 'cargoStartLocal'
    static final String STOP_LOCAL = 'cargoStopLocal'
    static final String CARGO_CONFIGURATION_NAME = 'cargo'
    static final String ACTION_CONVENTION_MAPPING_PARAM = 'action'

    @Override
    void apply(Project project) {
        project.plugins.apply(WarPlugin.class)

        project.configurations.add(CARGO_CONFIGURATION_NAME).setVisible(false).setTransitive(true)
               .setDescription('The Cargo Ant libraries to be used for this project.')

        CargoPluginConvention cargoConvention = new CargoPluginConvention()
        project.convention.plugins.cargo = cargoConvention

        configureAbstractContainerTask(project, cargoConvention)
        configureDeployRemoteContainerTask(project, cargoConvention)
        configureUndeployRemoteContainerTask(project, cargoConvention)
        configureRedeployRemoteContainerTask(project, cargoConvention)
        configureStartLocalContainerTask(project, cargoConvention)
        configureStopLocalContainerTask(project, cargoConvention)
    }

    private void configureAbstractContainerTask(Project project, CargoPluginConvention cargoConvention) {
        project.tasks.withType(AbstractContainerTask.class).whenTaskAdded { AbstractContainerTask abstractContainerTask ->
            abstractContainerTask.dependsOn WarPlugin.WAR_TASK_NAME
            abstractContainerTask.conventionMapping.map('classpath') { project.configurations.getByName(CARGO_CONFIGURATION_NAME).asFileTree }
            abstractContainerTask.conventionMapping.map('webApp') { project.tasks.getByName(WarPlugin.WAR_TASK_NAME).archivePath }
            abstractContainerTask.conventionMapping.map('containerId') { CargoProjectProperty.getContainerId(project, cargoConvention) }
            abstractContainerTask.conventionMapping.map('port') { CargoProjectProperty.getPort(project, cargoConvention) }
            abstractContainerTask.conventionMapping.map('context') { CargoProjectProperty.getContext(project, cargoConvention) }
            abstractContainerTask.conventionMapping.map('wait') { CargoProjectProperty.getWait(project, cargoConvention) }
        }
    }

    private void configureDeployRemoteContainerTask(Project project, CargoPluginConvention cargoConvention) {
        setRemoteContainerConventionMapping(project, cargoConvention, Action.DEPLOY.name)
        addRemoteContainerTask(project, DEPLOY_REMOTE, 'Deploys WAR to remote container')
    }

    private void configureUndeployRemoteContainerTask(Project project, final CargoPluginConvention cargoConvention) {
        setRemoteContainerConventionMapping(project, cargoConvention, Action.UNDEPLOY.name)
        addRemoteContainerTask(project, UNDEPLOY_REMOTE, 'Undeploys WAR from remote container')
    }

    private void configureRedeployRemoteContainerTask(Project project, CargoPluginConvention cargoConvention) {
        setRemoteContainerConventionMapping(project, cargoConvention, Action.REDEPLOY.name)
        addRemoteContainerTask(project, REDEPLOY_REMOTE, 'Redeploys WAR to remote container')
    }

    private void configureStartLocalContainerTask(Project project, CargoPluginConvention cargoConvention) {
        setLocalContainerConventionMapping(project, cargoConvention, Action.START.name)
        addLocalContainerTask(project, START_LOCAL, 'Starts the container and deploys WAR to it')
    }

    private void configureStopLocalContainerTask(Project project, CargoPluginConvention cargoConvention) {
        setLocalContainerConventionMapping(project, cargoConvention, Action.STOP.name)
        addLocalContainerTask(project, STOP_LOCAL, 'Stops local container')
    }

    private void setRemoteContainerConventionMapping(Project project, CargoPluginConvention cargoConvention, String action) {
        project.tasks.withType(RemoteContainerTask.class).whenTaskAdded { RemoteContainerTask remoteContainerTask ->
            remoteContainerTask.conventionMapping.map(ACTION_CONVENTION_MAPPING_PARAM) { action }
            remoteContainerTask.conventionMapping.map('protocol') { CargoProjectProperty.getProtocol(project, cargoConvention) }
            remoteContainerTask.conventionMapping.map('hostname') { CargoProjectProperty.getHostname(project, cargoConvention) }
            remoteContainerTask.conventionMapping.map('username') { CargoProjectProperty.getUsername(project, cargoConvention) }
            remoteContainerTask.conventionMapping.map('password') { CargoProjectProperty.getPassword(project, cargoConvention) }
        }
    }

    private void addRemoteContainerTask(Project project, String taskName, String taskDescription) {
        RemoteContainerTask remoteContainerTask = project.tasks.add(taskName, RemoteContainerTask.class)
        remoteContainerTask.description = taskDescription
        remoteContainerTask.group = WarPlugin.WEB_APP_GROUP
    }

    private void setLocalContainerConventionMapping(Project project, CargoPluginConvention cargoConvention, String action) {
        project.tasks.withType(LocalContainerTask.class).whenTaskAdded { LocalContainerTask localContainerTask ->
            localContainerTask.conventionMapping.map(ACTION_CONVENTION_MAPPING_PARAM) { action }
            localContainerTask.conventionMapping.map('logLevel') { CargoProjectProperty.getLogLevel(project, cargoConvention) }
            localContainerTask.conventionMapping.map('homeDir') { CargoProjectProperty.getHomeDir(project, cargoConvention) }
        }
    }

    private void addLocalContainerTask(Project project, String taskName, String taskDescription) {
        LocalContainerTask localContainerTask = project.tasks.add(taskName, LocalContainerTask.class)
        localContainerTask.description = taskDescription
        localContainerTask.group = WarPlugin.WEB_APP_GROUP
    }
}
