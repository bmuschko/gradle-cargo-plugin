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

import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Abstract container task.
 *
 * @author Benjamin Muschko
 */
abstract class AbstractContainerTask extends DefaultTask {
    static final Logger LOGGER = LoggerFactory.getLogger(AbstractContainerTask.class)
    static final CARGO_TASKS = 'cargo.tasks'
    static final CARGO_SERVLET_PORT = 'cargo.servlet.port'
    static final CARGO_CONTEXT = 'context'
    static final DEPLOYABLE_TYPE_WAR = 'war'
    String containerId
    String action
    Integer port
    String context
    @InputFiles FileCollection classpath
    @InputFile File webApp

    @TaskAction
    void start() {
        validateConfiguration()
        runAction()
    }

    void validateConfiguration() {
        if(!getWebApp() || !getWebApp().exists()) {
            throw new InvalidUserDataException("Web application WAR "
                    + (getWebApp() == null ? "null" : getWebApp().canonicalPath)
                    + " does not exist")
        }
        else {
            LOGGER.info "Web application WAR = ${getWebApp().canonicalPath}"
        }

        if(!getContainerId() || !Container.CONTAINERS.containsKey(getContainerId())) {
            throw new InvalidUserDataException("Unsupported container ID '${getContainerId()}'. Please pick a valid one: ${Container.containerIds}")
        }
        else {
            LOGGER.info "Container ID = ${getContainerId()}"
        }
    }

    abstract void runAction()
}
