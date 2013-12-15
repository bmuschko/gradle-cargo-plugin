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

enum CargoPluginTask {
    DEPLOY_REMOTE('cargoDeployRemote', 'Deploys WAR to remote container.', RemoteContainerTask, Action.DEPLOY),
    UNDEPLOY_REMOTE('cargoUndeployRemote', 'Undeploys WAR from remote container.', RemoteContainerTask, Action.UNDEPLOY),
    REDEPLOY_REMOTE('cargoRedeployRemote', 'Redeploys WAR to remote container.', RemoteContainerTask, Action.REDEPLOY),
    RUN_LOCAL('cargoRunLocal', 'Starts the container, deploys WAR to it and wait for the user to press CTRL + C to stop.', LocalContainerTask, Action.RUN),
    START_LOCAL('cargoStartLocal', 'Starts the container, deploys WAR to it and then do other tasks (for example, execute tests).', LocalContainerTask, Action.START),
    STOP_LOCAL('cargoStopLocal', 'Stops local container.', LocalContainerTask, Action.STOP)

    final String name
    final String description
    final Class taskClass
    final Action action

    private CargoPluginTask(String name, String description, Class taskClass, Action action) {
        this.name = name
        this.description = description
        this.taskClass = taskClass
        this.action = action
    }

    static enum Action {
        DEPLOY('deploy'), UNDEPLOY('undeploy'), REDEPLOY('redeploy'), RUN('run'), START('start'), STOP('stop')

        final String name

        private Action(String name) {
            this.name = name
        }
    }
}
