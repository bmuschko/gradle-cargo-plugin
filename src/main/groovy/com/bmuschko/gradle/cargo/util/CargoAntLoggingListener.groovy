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
package com.bmuschko.gradle.cargo.util

import org.apache.tools.ant.BuildEvent
import org.apache.tools.ant.BuildListener
import org.apache.tools.ant.Project
import org.apache.tools.ant.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

/**
 * Cargo Ant build listener.
 */
class CargoAntLoggingListener implements BuildListener {
    Logger logger = Logging.getLogger(CargoAntLoggingListener)
    static final String ANT_CARGO_TASK_NAME = 'org.codehaus.cargo.ant.CargoTask'

    @Override
    void buildStarted(BuildEvent buildEvent) {}

    @Override
    void buildFinished(BuildEvent buildEvent) {}

    @Override
    void targetStarted(BuildEvent buildEvent) {}

    @Override
    void targetFinished(BuildEvent buildEvent) {}

    @Override
    void taskStarted(BuildEvent buildEvent) {}

    @Override
    void taskFinished(BuildEvent buildEvent) {}

    /**
     * Wraps the subclasses action code in a listener that raises info priority Ant task messages to
     * be lifecycle messages for Gradle. This allows messages from the Ant task, such as
     * "Press Ctrl-C to stop the container..." to be passed through.
     *
     * @param buildEvent Build event
     */
    @Override
    void messageLogged(BuildEvent buildEvent) {
        if(buildEvent.task && isCargoTask(buildEvent.task) && isInfoPriority(buildEvent.priority)) {
            logger.lifecycle(buildEvent.message)
        }
    }

    private boolean isCargoTask(Task task) {
        task.class.canonicalName == ANT_CARGO_TASK_NAME
    }

    private boolean isInfoPriority(int priority) {
        priority == Project.MSG_INFO
    }
}
