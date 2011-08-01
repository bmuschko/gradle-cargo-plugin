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

/**
 * Created by IntelliJ IDEA.
 * User: Ben
 * Date: 7/30/11
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
enum CargoPluginTask {
    DEPLOY_REMOTE('cargoDeployRemote', 'Deploys WAR to remote container.'),
    UNDEPLOY_REMOTE('cargoUndeployRemote', 'Undeploys WAR from remote container.'),
    REDEPLOY_REMOTE('cargoRedeployRemote', 'Redeploys WAR to remote container.'),
    RUN_LOCAL('cargoRunLocal', 'Starts the container, deploys WAR to it and wait for the user to press CTRL + C to stop.'),
    START_LOCAL('cargoStartLocal', 'Starts the container, deploys WAR to it and then do other tasks (for example, execute tests).'),
    STOP_LOCAL('cargoStopLocal', 'Stops local container.')

    final String name
    final String description

    private CargoPluginTask(String name, String description) {
        this.name = name
        this.description = description
    }
}
