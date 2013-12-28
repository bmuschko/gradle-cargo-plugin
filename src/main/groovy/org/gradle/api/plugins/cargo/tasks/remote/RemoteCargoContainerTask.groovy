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
package org.gradle.api.plugins.cargo.tasks.remote

import org.gradle.api.InvalidUserDataException
import org.gradle.api.plugins.cargo.Container
import org.gradle.api.plugins.cargo.tasks.AbstractCargoContainerTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * Deploys WAR to remote container.
 *
 * @author Benjamin Muschko
 */
class RemoteCargoContainerTask extends AbstractCargoContainerTask {
    static final String UNDEPLOY_ACTION = 'undeploy'

    /**
     * Protocol on which the container is listening to. Defaults to 'http'.
     */
    @Input
    String protocol = 'http'

    /**
     * Host name on which the container listens to. Defaults to 'localhost'.
     */
    @Input
    String hostname = 'localhost'

    /**
     * Username to use to authenticate against a remote container.
     */
    @Input
    @Optional
    String username

    /**
     * Password to use to authenticate against a remote container.
     */
    @Input
    @Optional
    String password

    @Override
    void validateConfiguration() {
        super.validateConfiguration()

        if(getAction() != UNDEPLOY_ACTION) {
            getDeployables().each { deployable ->
                if(deployable.file && !deployable.file.exists()) {
                    throw new InvalidUserDataException("Deployable "
                            + (deployable.file == null ? "null" : deployable.file.canonicalPath)
                            + " does not exist")
                }

                logger.info "Deployable artifacts = ${getDeployables().collect { it.file.canonicalPath }}"
            }
        }
    }

    @Override
    void runAction() {
        logger.info "Starting action '${getAction()}' for remote container '${Container.getContainerForId(getContainerId()).description}' on '${getProtocol()}://${getHostname()}:${getPort()}'"

        ant.taskdef(resource: AbstractCargoContainerTask.CARGO_TASKS, classpath: getClasspath().asPath)
        ant.cargo(containerId: getContainerId(), type: 'remote', action: getAction()) {
            configuration(type: 'runtime') {
                property(name: 'cargo.protocol', value: getProtocol())
                property(name: 'cargo.hostname', value: getHostname())
                property(name: AbstractCargoContainerTask.CARGO_SERVLET_PORT, value: getPort())
                setContainerSpecificProperties()

                if(getUsername() && getPassword()) {
                    property(name: 'cargo.remote.username', value: getUsername())
                    property(name: 'cargo.remote.password', value: getPassword())
                }

                getDeployables().each { deployable ->
                    if(deployable.context) {
                        // For the undeploy action do not set a file attribute
                        if(getAction() == UNDEPLOY_ACTION) {
                            ant.deployable(type: getDeployableType(deployable).filenameExtension) {
                                property(name: AbstractCargoContainerTask.CARGO_CONTEXT, value: deployable.context)
                            }
                        }
                        else {
                            ant.deployable(type: getDeployableType(deployable).filenameExtension, file: deployable.file) {
                                property(name: AbstractCargoContainerTask.CARGO_CONTEXT, value: deployable.context)
                            }
                        }
                    }
                    else {
                        ant.deployable(type: getDeployableType(deployable).filenameExtension, file: deployable.file)
                    }
                }
            }
        }
    }
}
