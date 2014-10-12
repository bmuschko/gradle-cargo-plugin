/*
 * Copyright 2014 the original author or authors.
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
import org.gradle.api.plugins.WarPlugin
import org.gradle.plugins.ear.EarPlugin

/**
 * Provides helper method for retrieving project information.
 *
 * @author Benjamin Muschko
 */
final class ProjectInfoHelper {
    private ProjectInfoHelper() {}

    static File getProjectDeployableFile(Project project) {
        if(project.plugins.hasPlugin(WarPlugin.WAR_TASK_NAME)) {
            return project.tasks.getByName(WarPlugin.WAR_TASK_NAME).archivePath
        }
        else if(project.plugins.hasPlugin(EarPlugin.EAR_TASK_NAME)) {
            return project.tasks.getByName(EarPlugin.EAR_TASK_NAME).archivePath
        }
    }
}
