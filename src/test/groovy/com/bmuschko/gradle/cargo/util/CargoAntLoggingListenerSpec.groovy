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
import org.apache.tools.ant.taskdefs.Copy
import org.codehaus.cargo.ant.CargoTask
import org.gradle.api.logging.Logger
import spock.lang.Specification

/**
 * Cargo Ant build listener unit tests.
 */
class CargoAntLoggingListenerSpec extends Specification {
    BuildListener buildListener

    def setup() {
        buildListener = new CargoAntLoggingListener()
    }

    void "Message not logged for non-Cargo Ant task"() {
        given:
            Task antCopyTask = new Copy()
            BuildEvent buildEvent = new BuildEvent(antCopyTask)
            buildEvent.message = 'Cargo started'
            Logger mockLogger = Mock(Logger)
            buildListener.logger = mockLogger
        when:
            buildListener.messageLogged(buildEvent)
        then:
            0 * mockLogger.lifecycle('Cargo started')
    }

    void "Message not logged for Cargo Ant task with debug priority"() {
        given:
            Task antCargoTask = new CargoTask()
            BuildEvent buildEvent = new BuildEvent(antCargoTask)
            buildEvent.message = 'Cargo started'
            buildEvent.priority = Project.MSG_DEBUG
            Logger mockLogger = Mock(Logger)
            buildListener.logger = mockLogger
        when:
            buildListener.messageLogged(buildEvent)
        then:
            0 * mockLogger.lifecycle('Cargo started')
    }

    void "Message logged for Cargo Ant task with info priority"() {
        given:
            Task antCargoTask = new CargoTask()
            BuildEvent buildEvent = new BuildEvent(antCargoTask)
            buildEvent.message = 'Cargo started'
            buildEvent.priority = Project.MSG_INFO
            Logger mockLogger = Mock(Logger)
            buildListener.logger = mockLogger
        when:
            buildListener.messageLogged(buildEvent)
        then:
            1 * mockLogger.lifecycle('Cargo started')
    }
}
