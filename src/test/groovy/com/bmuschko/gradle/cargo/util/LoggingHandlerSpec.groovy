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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Logging handler unit tests.
 *
 * @author Benjamin Muschko
 */
class LoggingHandlerSpec extends Specification {
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    void "Closure is executed"() {
        expect:
            project.ant.project.buildListeners.find { it.class == org.apache.tools.ant.BuildListener } == null
            boolean test = false
        when:
            LoggingHandler.withAntLoggingListener(project.ant) {
                test = true
            }
        then:
            test
            project.ant.project.buildListeners.find { it.class == org.apache.tools.ant.BuildListener } == null
    }
}
