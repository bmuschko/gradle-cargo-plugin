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
import org.gradle.api.plugins.WarPlugin
import org.gradle.api.plugins.cargo.convention.CargoPluginConvention
import org.gradle.api.plugins.cargo.convention.Deployable
import org.gradle.plugins.ear.EarPlugin
import org.gradle.api.plugins.cargo.property.*

/**
 * <p>A {@link org.gradle.api.Plugin} that provides tasks for deploying WAR/EAR files to local and remote web containers.</p>
 *
 * @author Benjamin Muschko
 */
class CargoPlugin implements Plugin<Project> {
    static final String CARGO_CONFIGURATION_NAME = 'cargo'
    static final String CARGO_TASK_GROUP = 'deployment'
    static final String ACTION_CONVENTION_MAPPING_PARAM = 'action'

    @Override
    void apply(Project project) {
        project.configurations.add(CARGO_CONFIGURATION_NAME).setVisible(false).setTransitive(true)
               .setDescription('The Cargo Ant libraries to be used for this project.')

        CargoPluginConvention cargoConvention = new CargoPluginConvention()
        project.convention.plugins.cargo = cargoConvention

        configureAbstractContainerTask(project, cargoConvention)
        configureLocalContainerConventionMapping(project, cargoConvention)
        configureDeployRemoteContainerTask(project, cargoConvention)
        configureUndeployRemoteContainerTask(project, cargoConvention)
        configureRedeployRemoteContainerTask(project, cargoConvention)
        configureRunLocalContainerTask(project, cargoConvention)
        configureStartLocalContainerTask(project, cargoConvention)
        configureStopLocalContainerTask(project, cargoConvention)
    }

    private void configureAbstractContainerTask(Project project, CargoPluginConvention cargoConvention) {
        project.tasks.withType(AbstractContainerTask).whenTaskAdded { AbstractContainerTask abstractContainerTask ->
            abstractContainerTask.conventionMapping.map('classpath') { project.configurations.getByName(CARGO_CONFIGURATION_NAME).asFileTree }
            abstractContainerTask.conventionMapping.map('containerId') {
                CargoProjectProperty.getTypedProperty(project, AbstractContainerTaskProperty.CONTAINER_ID, cargoConvention.containerId)
            }
            abstractContainerTask.conventionMapping.map('port') {
                CargoProjectProperty.getTypedProperty(project, AbstractContainerTaskProperty.PORT, cargoConvention.port)
            }
            abstractContainerTask.conventionMapping.map('deployables') { resolveDeployables(project, cargoConvention) }
        }
    }

    private void configureLocalContainerConventionMapping(Project project, CargoPluginConvention cargoConvention) {
        project.tasks.withType(LocalContainerTask).whenTaskAdded { LocalContainerTask localContainerTask ->
            localContainerTask.conventionMapping.map('jvmArgs') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.JVM_ARGS, cargoConvention.local.jvmArgs)
            }
            localContainerTask.conventionMapping.map('logLevel') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.LOG_LEVEL, cargoConvention.local.logLevel)
            }
            localContainerTask.conventionMapping.map('homeDir') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.HOME_DIR, cargoConvention.local.homeDir)
            }
            localContainerTask.conventionMapping.map('output') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.OUTPUT, cargoConvention.local.output)
            }
            localContainerTask.conventionMapping.map('logFile') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.LOG, cargoConvention.local.log)
            }
            localContainerTask.conventionMapping.map('rmiPort') {
                CargoProjectProperty.getTypedProperty(project, LocalContainerTaskProperty.RMI_PORT, cargoConvention.local.rmiPort)
            }
            localContainerTask.conventionMapping.map('zipUrlInstaller') {
                cargoConvention.local.zipUrlInstaller
            }
            localContainerTask.conventionMapping.map('configFiles') {
                cargoConvention.local.configFiles
            }
        }
    }

    private void setRemoteContainerConventionMapping(Project project, CargoPluginConvention cargoConvention, Action action) {
        project.tasks.withType(RemoteContainerTask).whenTaskAdded { RemoteContainerTask remoteContainerTask ->
            remoteContainerTask.conventionMapping.map(ACTION_CONVENTION_MAPPING_PARAM) { action.name }
            remoteContainerTask.conventionMapping.map('protocol') {
                CargoProjectProperty.getTypedProperty(project, RemoteContainerTaskProperty.PROTOCOL, cargoConvention.remote.protocol)
            }
            remoteContainerTask.conventionMapping.map('hostname') {
                CargoProjectProperty.getTypedProperty(project, RemoteContainerTaskProperty.HOSTNAME, cargoConvention.remote.hostname)
            }
            remoteContainerTask.conventionMapping.map('username') {
                CargoProjectProperty.getTypedProperty(project, RemoteContainerTaskProperty.USERNAME, cargoConvention.remote.username)
            }
            remoteContainerTask.conventionMapping.map('password') {
                CargoProjectProperty.getTypedProperty(project, RemoteContainerTaskProperty.PASSWORD, cargoConvention.remote.password)
            }
        }
    }

    private void setDefaultLocalContainerConventionMapping(Project project, CargoPluginConvention cargoConvention, Action action) {
        project.tasks.withType(LocalContainerTask).whenTaskAdded { LocalContainerTask localContainerTask ->
            localContainerTask.conventionMapping.map(ACTION_CONVENTION_MAPPING_PARAM) { action.name }
        }
    }

    private void setLocalJettyConventionMapping(Project project, CargoPluginConvention cargoConvention, Action action) {
        project.tasks.withType(LocalJettyTask).whenTaskAdded { LocalJettyTask localJettyTask ->
            localJettyTask.conventionMapping.map(ACTION_CONVENTION_MAPPING_PARAM) { action.name }
            localJettyTask.conventionMapping.map('createContextXml') {
                CargoProjectProperty.getTypedProperty(project, LocalJettyTaskProperty.CREATE_CONTEXT_XML, cargoConvention.local.jetty.createContextXml)
            }
            localJettyTask.conventionMapping.map('sessionPath') {
                CargoProjectProperty.getTypedProperty(project, LocalJettyTaskProperty.SESSION_PATH, cargoConvention.local.jetty.sessionPath)
            }
            localJettyTask.conventionMapping.map('useFileMappedBuffer') {
                CargoProjectProperty.getTypedProperty(project, LocalJettyTaskProperty.USE_FILE_MAPPED_BUFFER, cargoConvention.local.jetty.useFileMappedBuffer)
            }
        }
    }

    private void setLocalJonasConventionMapping(Project project, CargoPluginConvention cargoConvention, Action action) {
        project.tasks.withType(LocalJonasTask).whenTaskAdded { LocalJonasTask localJonasTask ->
            localJonasTask.conventionMapping.map(ACTION_CONVENTION_MAPPING_PARAM) { action.name }
            localJonasTask.conventionMapping.map('jmsPort') {
                CargoProjectProperty.getTypedProperty(project, LocalJonasTaskProperty.JMS_PORT, cargoConvention.local.jonas.jmsPort)
            }
            localJonasTask.conventionMapping.map('serverName') {
                CargoProjectProperty.getTypedProperty(project, LocalJonasTaskProperty.SERVER_NAME, cargoConvention.local.jonas.serverName)
            }
            localJonasTask.conventionMapping.map('servicesList') {
                CargoProjectProperty.getTypedProperty(project, LocalJonasTaskProperty.SERVICES_LIST, cargoConvention.local.jonas.servicesList)
            }
            localJonasTask.conventionMapping.map('domainName') {
                CargoProjectProperty.getTypedProperty(project, LocalJonasTaskProperty.DOMAIN_NAME, cargoConvention.local.jonas.domainName)
            }
        }
    }

    private void setLocalJRunConventionMapping(Project project, CargoPluginConvention cargoConvention, Action action) {
        project.tasks.withType(LocalJRunTask).whenTaskAdded { LocalJRunTask localJRunTask ->
            localJRunTask.conventionMapping.map(ACTION_CONVENTION_MAPPING_PARAM) { action.name }
            localJRunTask.conventionMapping.map('home') {
                CargoProjectProperty.getTypedProperty(project, LocalJRunTaskProperty.HOME, cargoConvention.local.jrun.home)
            }
        }
    }

    private void setLocalTomcatConventionMapping(Project project, CargoPluginConvention cargoConvention, Action action) {
        project.tasks.withType(LocalTomcatTask).whenTaskAdded { LocalTomcatTask localTomcatTask ->
            localTomcatTask.conventionMapping.map(ACTION_CONVENTION_MAPPING_PARAM) { action.name }
            localTomcatTask.conventionMapping.map('webappsDir') {
                CargoProjectProperty.getTypedProperty(project, LocalTomcatTaskProperty.WEBAPPS_DIRECTORY, cargoConvention.local.tomcat.webappsDir)
            }
            localTomcatTask.conventionMapping.map('copyWars') {
                CargoProjectProperty.getTypedProperty(project, LocalTomcatTaskProperty.COPY_WARS, cargoConvention.local.tomcat.copyWars)
            }
            localTomcatTask.conventionMapping.map('contextReloadable') {
                CargoProjectProperty.getTypedProperty(project, LocalTomcatTaskProperty.CONTEXT_RELOADABLE, cargoConvention.local.tomcat.contextReloadable)
            }
            localTomcatTask.conventionMapping.map('ajpPort') {
                CargoProjectProperty.getTypedProperty(project, LocalTomcatTaskProperty.AJP_PORT, cargoConvention.local.tomcat.ajpPort)
            }
        }
    }

    private void setLocalWeblogicConventionMapping(Project project, CargoPluginConvention cargoConvention, Action action) {
        project.tasks.withType(LocalWeblogicTask).whenTaskAdded { LocalWeblogicTask localWeblogicTask ->
            localWeblogicTask.conventionMapping.map(ACTION_CONVENTION_MAPPING_PARAM) { action.name }
            localWeblogicTask.conventionMapping.map('adminUser') {
                CargoProjectProperty.getTypedProperty(project, LocalWeblogicTaskProperty.ADMIN_USER, cargoConvention.local.weblogic.adminUser)
            }
            localWeblogicTask.conventionMapping.map('adminPassword') {
                CargoProjectProperty.getTypedProperty(project, LocalWeblogicTaskProperty.ADMIN_PASSWORD, cargoConvention.local.weblogic.adminPassword)
            }
            localWeblogicTask.conventionMapping.map('beaHome') {
                CargoProjectProperty.getTypedProperty(project, LocalWeblogicTaskProperty.BEA_HOME, cargoConvention.local.weblogic.beaHome)
            }
            localWeblogicTask.conventionMapping.map('server') {
                CargoProjectProperty.getTypedProperty(project, LocalWeblogicTaskProperty.SERVER, cargoConvention.local.weblogic.server)
            }
        }
    }

    private void configureDeployRemoteContainerTask(Project project, CargoPluginConvention cargoConvention) {
        setRemoteContainerConventionMapping(project, cargoConvention, Action.DEPLOY)
        addContainerTask(project, RemoteContainerTask, CargoPluginTask.DEPLOY_REMOTE)
    }

    private void configureUndeployRemoteContainerTask(Project project, CargoPluginConvention cargoConvention) {
        setRemoteContainerConventionMapping(project, cargoConvention, Action.UNDEPLOY)
        addContainerTask(project, RemoteContainerTask, CargoPluginTask.UNDEPLOY_REMOTE)
    }

    private void configureRedeployRemoteContainerTask(Project project, CargoPluginConvention cargoConvention) {
        setRemoteContainerConventionMapping(project, cargoConvention, Action.REDEPLOY)
        addContainerTask(project, RemoteContainerTask, CargoPluginTask.REDEPLOY_REMOTE)
    }

    private void configureRunLocalContainerTask(Project project, CargoPluginConvention cargoConvention) {
        configureLocalContainer(project, cargoConvention, Action.RUN, CargoPluginTask.RUN_LOCAL)
    }

    private void configureStartLocalContainerTask(Project project, CargoPluginConvention cargoConvention) {
        configureLocalContainer(project, cargoConvention, Action.START, CargoPluginTask.START_LOCAL)
    }

    private void configureStopLocalContainerTask(Project project, CargoPluginConvention cargoConvention) {
        configureLocalContainer(project, cargoConvention, Action.STOP, CargoPluginTask.STOP_LOCAL)
    }

    private void configureLocalContainer(Project project, CargoPluginConvention cargoConvention, Action action, CargoPluginTask task) {
        project.afterEvaluate {
            if(!cargoConvention.containerId) {
                throw new InvalidUserDataException('Container ID was not defined.')
            }

            LocalContainerTaskMapping mapping = LocalContainerTaskMapping.getLocalContainerTaskMappingForContainerId(cargoConvention.containerId)

            switch(mapping) {
                case LocalContainerTaskMapping.JETTY: setLocalJettyConventionMapping(project, cargoConvention, action)
                                                      break
                case LocalContainerTaskMapping.JONAS: setLocalJonasConventionMapping(project, cargoConvention, action)
                                                      break
                case LocalContainerTaskMapping.JRUN: setLocalJRunConventionMapping(project, cargoConvention, action)
                                                     break
                case LocalContainerTaskMapping.TOMCAT: setLocalTomcatConventionMapping(project, cargoConvention, action)
                                                       break
                case LocalContainerTaskMapping.WEBLOGIC: setLocalWeblogicConventionMapping(project, cargoConvention, action)
                                                         break
                default: setDefaultLocalContainerConventionMapping(project, cargoConvention, action)
            }

            addContainerTask(project, mapping.taskClass, task)
        }
    }

    private void addContainerTask(Project project, Class taskClass, CargoPluginTask task) {
        def containerTask = project.tasks.add(task.name, taskClass)
        containerTask.description = task.description
        containerTask.group = CARGO_TASK_GROUP
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
