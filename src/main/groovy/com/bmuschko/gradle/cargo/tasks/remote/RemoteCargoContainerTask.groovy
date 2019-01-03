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
package com.bmuschko.gradle.cargo.tasks.remote

import com.bmuschko.gradle.cargo.DeployableType
import com.bmuschko.gradle.cargo.DeployableTypeFactory
import com.bmuschko.gradle.cargo.convention.Deployable
import com.bmuschko.gradle.cargo.tasks.AbstractCargoContainerTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

/**
 * Deploys WAR to remote container.
 */
class RemoteCargoContainerTask extends AbstractCargoContainerTask {
    static final String UNDEPLOY_ACTION = 'undeploy'

    /**
     * Protocol on which the container is listening to. Defaults to 'http'.
     */
    @Input
    Property<String> protocol = project.objects.property(String)

    /**
     * Host name on which the container listens to. Defaults to 'localhost'.
     */
    @Input
    Property<String> hostname = project.objects.property(String)

    /**
     * Username to use to authenticate against a remote container.
     */
    @Input
    Property<String> username = project.objects.property(String)

    /**
     * Password to use to authenticate against a remote container.
     */
    @Input
    Property<String> password = project.objects.property(String)

    RemoteCargoContainerTask() {
        protocol.set("http")
        hostname.set("localhost")
    }

    @Override
    void validateConfiguration() {
        super.validateConfiguration()

        if(getAction() != UNDEPLOY_ACTION) {
            getDeployables().get().each { deployable ->
                if(deployable.getFile().present && !deployable.getFile().get().exists()) {
                    throw new InvalidUserDataException("Deployable "
                            + (!deployable.getFile().present ? "null" : deployable.file.get().canonicalPath)
                            + " does not exist")
                }

                if(DeployableType.EXPLODED == DeployableTypeFactory.instance.getType(deployable.getFile().get())) {
                    throw new InvalidUserDataException("Deployable type: EXPLODED is invalid for remote deployment")
                }
            }
            logger.info "Deployable artifacts = ${getDeployables().get().collect { it.file.canonicalPath }}"
        }
    }

    @Override
    void runAction() {
        logger.info "Starting action '${getAction()}' for remote container '${getContainerId().get()}' on '${getProtocol().get()}://${getHostname().get()}:${getPort().get()}'"

        ant.taskdef(resource: CARGO_TASKS, classpath: getClasspath().asPath)
        ant.cargo(containerId: getContainerId(), type: 'remote', action: getAction()) {
            configuration(type: 'runtime') {
                property(name: 'cargo.protocol', value: getProtocol().get())
                property(name: 'cargo.hostname', value: getHostname().get())
                property(name: CARGO_SERVLET_PORT, value: getPort().get())
                setContainerSpecificProperties()

                if(getUsername().present && getPassword().present) {
                    property(name: 'cargo.remote.username', value: getUsername().get())
                    property(name: 'cargo.remote.password', value: getPassword().get())
                }

                getDeployables().each { Deployable deployable ->
                    DeployableType deployableType = DeployableTypeFactory.instance.getType(deployable.file.get())

                    if(deployable.getContext().present) {
                        // For the undeploy action do not set a file attribute
                        if(getAction() == UNDEPLOY_ACTION) {
                            ant.deployable(type: deployableType.getType()) {
                                property(name: CARGO_CONTEXT, value: deployable.context.get())
                            }
                        }
                        else {
                            ant.deployable(type: deployableType.getType(), file: deployable.getFile().get()) {
                                property(name: CARGO_CONTEXT, value: deployable.context.get())
                            }
                        }
                    }
                    else {
                        ant.deployable(type: deployableType.getType(), file: deployable.getFile().get())
                    }
                }
            }
        }
    }
}
