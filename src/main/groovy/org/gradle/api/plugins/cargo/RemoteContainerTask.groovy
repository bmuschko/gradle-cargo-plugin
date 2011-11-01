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

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Deploys WAR to remote container.
 *
 * @author Benjamin Muschko
 */
class RemoteContainerTask extends AbstractContainerTask {
    static final Logger LOGGER = LoggerFactory.getLogger(RemoteContainerTask.class)
    String protocol
    String hostname
    String username
    String password

    @Override
    void runAction() {
        if(LOGGER.isInfoEnabled()) {
            LOGGER.info "Starting action '${getAction()}' for remote container '${Container.getContainerForId(getContainerId()).description}' on '${getProtocol()}://${getHostname()}:${getPort()}'"
        }

        ant.taskdef(resource: CARGO_TASKS, classpath: getClasspath().asPath)
        ant.cargo(containerId: getContainerId(), type: 'remote', action: getAction()) {
            configuration(type: 'runtime') {
                property(name: 'cargo.protocol', value: getProtocol())
                property(name: 'cargo.hostname', value: getHostname())
                property(name: CARGO_SERVLET_PORT, value: getPort())

                if(getUsername() && getPassword()) {
                    property(name: 'cargo.remote.username', value: getUsername())
                    property(name: 'cargo.remote.password', value: getPassword())
                }

                if(getContext()) {
                    deployable(type: getDeployableType().filenameExtension, file: getDeployable()) {
                        property(name: CARGO_CONTEXT, value: getContext())
                    }
                }
                else {
                    deployable(type: getDeployableType().filenameExtension, file: getDeployable())
                }
            }
        }
    }
}
