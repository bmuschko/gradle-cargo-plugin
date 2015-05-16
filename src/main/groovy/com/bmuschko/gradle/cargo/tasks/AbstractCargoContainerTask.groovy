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
package com.bmuschko.gradle.cargo.tasks

import com.bmuschko.gradle.cargo.Container
import com.bmuschko.gradle.cargo.convention.Deployable
import com.bmuschko.gradle.cargo.util.LoggingHandler
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Abstract container task.
 */
abstract class AbstractCargoContainerTask extends DefaultTask {
    static final String CARGO_TASK_GROUP = 'deployment'
    static final CARGO_TASKS = 'cargo.tasks'
    static final CARGO_SERVLET_PORT = 'cargo.servlet.port'
    static final CARGO_CONTEXT = 'context'

    /**
     * The Cargo container identifier.
     */
    @Input
    String containerId

    /**
     * The action to run for the container.
     */
    @Input
    String action

    /**
     * Port on which the Servlet/JSP container listens to.
     */
    @Input
    Integer port = 8080

    @Input
    @Optional
    String context

    /**
     * The classpath containing the Cargo Ant tasks.
     */
    @InputFiles
    FileCollection classpath

    /**
     * The list of deployable artifacts.
     */
    @Input
    @Optional
    List<Deployable> deployables = []

    /**
     * Container properties.
     */
    @Input
    Map<String, Object> containerProperties = [:]

    AbstractCargoContainerTask() {
        group = CARGO_TASK_GROUP

        // No matter what the inputs and outputs make sure that run tasks are never up-to-date
        outputs.upToDateWhen {
            false
        }
    }

    @TaskAction
    void start() {
        validateConfiguration()

        LoggingHandler.withAntLoggingListener(ant) {
            runAction()
        }
    }

    void validateConfiguration() {
        if(!getDeployables()) {
            throw new InvalidUserDataException('No deployables assigned!')
        }

        if(!getContainerId() || !Container.CONTAINERS.containsKey(getContainerId())) {
            throw new InvalidUserDataException("Unsupported container ID '${getContainerId()}'. Please pick a valid one: ${Container.containerIds}")
        }
        else {
            logger.info "Container ID = ${getContainerId()}"
        }
    }

    protected void setContainerSpecificProperties() {
        logger.info "Container properties = ${getContainerProperties()}"

        getContainerProperties().each { key, value ->
            ant.property(name: key, value: value)
        }
    }

    protected abstract void runAction()
}
