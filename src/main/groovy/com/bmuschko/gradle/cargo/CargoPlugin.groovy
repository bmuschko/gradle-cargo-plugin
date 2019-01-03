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
package com.bmuschko.gradle.cargo

import com.bmuschko.gradle.cargo.convention.CargoLocalTaskConvention
import com.bmuschko.gradle.cargo.convention.CargoPluginExtension
import com.bmuschko.gradle.cargo.convention.CargoRemoteTaskConvention
import com.bmuschko.gradle.cargo.tasks.local.*
import com.bmuschko.gradle.cargo.tasks.remote.CargoDeployRemote
import com.bmuschko.gradle.cargo.tasks.remote.CargoRedeployRemote
import com.bmuschko.gradle.cargo.tasks.remote.CargoUndeployRemote
import com.bmuschko.gradle.cargo.tasks.remote.RemoteCargoContainerTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionAware

/**
 * <p>A {@link org.gradle.api.Plugin} that provides pre-configured tasks for deploying WAR/EAR files to local and remote web containers.</p>
 */
class CargoPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply(CargoBasePlugin)

        CargoLocalTaskConvention localTaskConvention = new CargoLocalTaskConvention(project)
        CargoRemoteTaskConvention remoteTaskConvention = new CargoRemoteTaskConvention(project.objects)

        // add extensions to the base plugin's Extension, add local and remote.
        project.extensions.configure(CargoPluginExtension, { ExtensionAware plugin ->
            plugin.extensions.add("local", localTaskConvention, )
            plugin.extensions.add("remote", remoteTaskConvention)
        })

        configureRemoteContainerConventionMapping(project, remoteTaskConvention)
        configureLocalContainerConventionMapping(project, localTaskConvention)

        configureRemoteContainerTasks(project)
        configureLocalContainerTasks(project)
    }

    private void configureLocalContainerConventionMapping(Project project, CargoLocalTaskConvention local) {
        project.tasks.withType(LocalCargoContainerTask, { LocalCargoContainerTask task ->
            task.jvmArgs.set(local.jvmArgs)
            task.logLevel.set(local.logLevel)
            task.homeDir.set(local.homeDir)
            task.configType.set(local.configType)
            task.configHomeDir.set(local.configHomeDir)
            task.outputFile.set(local.outputFile)
            task.logFile.set(local.logFile)
            task.rmiPort.set(local.rmiPort)
            task.configFiles.set(local.configFiles)
            task.files.set(local.files)
            task.extraClasspath.setFrom(local.extraClasspath)
            task.sharedClasspath.setFrom(local.sharedClasspath)
            task.containerProperties.set(local.containerProperties)
            task.systemProperties.set(local.systemProperties)

            task.zipUrlInstaller.set(local.zipUrlInstaller)
        })
    }

    private void configureRemoteContainerConventionMapping(Project project, CargoRemoteTaskConvention remote) {
        project.tasks.withType(RemoteCargoContainerTask, { RemoteCargoContainerTask task ->
            task.protocol.set(remote.protocol)
            task.hostname.set(remote.hostname)
            task.username.set(remote.username)
            task.password.set(remote.password)

            task.containerProperties.set(remote.containerProperties)
        })
    }

    private void configureRemoteContainerTasks(Project project) {
        project.tasks.register('cargoDeployRemote', CargoDeployRemote)
        project.tasks.register('cargoUndeployRemote', CargoUndeployRemote)
        project.tasks.register('cargoRedeployRemote', CargoRedeployRemote)
    }

    private void configureLocalContainerTasks(Project project) {
        project.tasks.register('cargoRunLocal', CargoRunLocal)
        project.tasks.register('cargoStartLocal', CargoStartLocal)
        project.tasks.register('cargoStopLocal', CargoStopLocal)
        project.tasks.register('cargoRedeployLocal', CargoRedeployLocal)
        project.tasks.register('cargoConfigureLocal', CargoConfigureLocal)
    }
}
