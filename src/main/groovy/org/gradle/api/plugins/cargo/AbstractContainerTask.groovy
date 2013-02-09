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

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.cargo.convention.Deployable
import org.gradle.api.plugins.cargo.util.FilenameUtils
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

/**
 * Abstract container task.
 *
 * @author Benjamin Muschko
 */
@Slf4j
abstract class AbstractContainerTask extends DefaultTask {
    static final CARGO_TASKS = 'cargo.tasks'
    static final CARGO_SERVLET_PORT = 'cargo.servlet.port'
    static final CARGO_CONTEXT = 'context'
    String containerId
    String action
    Integer port
    String context
    @InputFiles FileCollection classpath
    @Input List<Deployable> deployables

    @TaskAction
    void start() {
        validateConfiguration()
        runAction()
    }

    void validateConfiguration() {
        if(!getDeployables()) {
            throw new InvalidUserDataException('No deployables assigned!')
        }

        getDeployables().each { deployable ->
            if(deployable.file && !deployable.file.exists()) {
                throw new InvalidUserDataException("Deployable "
                + (deployable.file == null ? "null" : deployable.file.canonicalPath)
                + " does not exist")
            }
        }

        log.info "Deployable artifacts = ${getDeployables().collect { it.file.canonicalPath }}"

        if(!getContainerId() || !Container.CONTAINERS.containsKey(getContainerId())) {
            throw new InvalidUserDataException("Unsupported container ID '${getContainerId()}'. Please pick a valid one: ${Container.containerIds}")
        }
        else {
            log.info "Container ID = ${getContainerId()}"
        }
    }

    DeployableType getDeployableType(Deployable deployable) {
        String filenameExtension = FilenameUtils.getExtension(deployable.file.canonicalPath)
        DeployableType.getDeployableTypeForFilenameExtension(filenameExtension)
    }

    // Wraps the subclasses action code in a listener that raises info priority ant task messages to
    // be lifecycle messages for gradle.  This allows messages from the ant task, such as
    // "Press Ctrl-C to stop the container..." to be passed through.
    final void runAction() {
        def localThread = Thread.currentThread()
        def listener = [
                buildFinished: {},
                buildStarted: {},
                messageLogged: {e ->
                    if (    e.getTask() != null &&
                            localThread == Thread.currentThread() &&
                            e.getTask().class.getCanonicalName() == 'org.codehaus.cargo.ant.CargoTask' &&
                            e.getPriority() == org.apache.tools.ant.Project.MSG_INFO ) {
                        logger.lifecycle(e.getMessage())
                    }
                },
                targetFinished: {},
                targetStarted: {},
                taskFinished: {},
                taskStarted: {},
        ] as org.apache.tools.ant.BuildListener
        try {
            ant.project.addBuildListener(listener)
            subclassRunAction()
        }
        finally {
            ant.project.removeBuildListener(listener)
        }

    }

    abstract void subclassRunAction()
}
