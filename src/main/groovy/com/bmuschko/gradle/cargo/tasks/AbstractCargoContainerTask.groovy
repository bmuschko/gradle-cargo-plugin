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

import com.bmuschko.gradle.cargo.convention.Deployable
import com.bmuschko.gradle.cargo.util.LoggingHandler
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
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
    Property<String> containerId = project.objects.property(String)

    /**
     * The action to run for the container.
     */
    @Input
    String action

    /**
     * Port on which the Servlet/JSP container listens to.
     */
    @Input
    Property<Integer> port = project.objects.property(Integer)

    @Input
    @Optional
    Property<String> context = project.objects.property(String)

    /**
     * The classpath containing the Cargo Ant tasks.
     */
    ConfigurableFileCollection classpath = project.layout.configurableFiles()

    @Internal
    ConfigurableFileCollection alternativeClasspath = project.layout.configurableFiles()

    @InputFiles
    ConfigurableFileCollection getClasspath() {
        if(classpath.empty) {
            alternativeClasspath
        } else {
            classpath
        }
    }

    /**
     * The list of deployable artifacts.
     */
    @Nested
    ListProperty<Deployable> deployables = project.objects.listProperty(Deployable)

    /**
     * Container properties.
     */
    @Input
    MapProperty<String, Object> containerProperties = project.objects.mapProperty(String, Object)

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
        if(!containerId.isPresent()) {
            throw new InvalidUserDataException('Container ID was not defined.')
        }
    }

    protected void setContainerSpecificProperties() {
        logger.info "Container properties = ${getContainerProperties().get()}"

        getContainerProperties().get().each { key, value ->
            ant.property(name: key, value: value)
        }
    }

    protected abstract void runAction()
}
