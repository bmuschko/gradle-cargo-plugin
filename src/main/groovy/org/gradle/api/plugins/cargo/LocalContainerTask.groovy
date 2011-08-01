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

import org.gradle.api.tasks.InputDirectory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Deploys WAR to local container.
 *
 * @author Benjamin Muschko
 */
class LocalContainerTask extends AbstractContainerTask {
    static final Logger LOGGER = LoggerFactory.getLogger(LocalContainerTask.class)
    String logLevel
    @InputDirectory File homeDir

    @Override
    void runAction() {
        if(LOGGER.isInfoEnabled()) {
            LOGGER.info "Starting action '${getAction()}' for local container '${Container.getContainerForId(getContainerId()).description}'"
        }

        ant.taskdef(resource: CARGO_TASKS, classpath: getClasspath().asPath)
        ant.cargo(containerId: getContainerId(), home: getHomeDir().canonicalPath, action: getAction()) {
            configuration {
                property(name: CARGO_SERVLET_PORT, value: getPort())

                if(getLogLevel()) {
                    property(name: 'cargo.logging', value: getLogLevel())
                }

                if(getContext()) {
                    deployable(type: DEPLOYABLE_TYPE_WAR, file: getWebApp()) {
                        property(name: CARGO_CONTEXT, value: getContext())
                    }
                }
                else {
                    deployable(type: DEPLOYABLE_TYPE_WAR, file: getWebApp())
                }

                setContainerSpecificProperties()
            }
        }
    }

    void setContainerSpecificProperties() {}
}
