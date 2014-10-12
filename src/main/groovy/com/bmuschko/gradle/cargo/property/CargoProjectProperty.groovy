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
package com.bmuschko.gradle.cargo.property

import org.gradle.api.GradleException
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

/**
 * Retrieves Cargo project property.
 *
 * @author Benjamin Muschko
 */
class CargoProjectProperty {
    static getTypedProperty(Project project, TaskProperty taskProperty, conventionValue) {
        switch(taskProperty.type) {
            case PropertyDataType.STRING: return getStringProperty(project, taskProperty.name, conventionValue)
            case PropertyDataType.INTEGER: return getIntegerProperty(project, taskProperty.name, conventionValue)
            case PropertyDataType.BOOLEAN: return getBooleanProperty(project, taskProperty.name, conventionValue)
            case PropertyDataType.FILE: return getFileProperty(project, taskProperty.name, conventionValue)
            default: throw new GradleException("Unknown property data type: ${taskProperty.type}")
        }
    }

    private static String getProjectProperty(Project project, String name) {
        project.hasProperty(name) ? project.property(name) : null
    }

    private static String getStringProperty(Project project, String propertyName, String conventionValue) {
        String projectProperty = getProjectProperty(project, propertyName)
        projectProperty ?: conventionValue
    }

    private static Integer getIntegerProperty(Project project, String propertyName, Integer conventionValue) {
        String projectProperty = getProjectProperty(project, propertyName)

        if(projectProperty) {
            try {
                return projectProperty.toInteger()
            }
            catch(NumberFormatException e) {
                throw new InvalidUserDataException("Invalid value provided as project property: '$projectProperty'", e)
            }
        }

        conventionValue
    }

    private static Boolean getBooleanProperty(Project project, String propertyName, Boolean conventionValue) {
        String projectProperty = getProjectProperty(project, propertyName)
        projectProperty ? projectProperty.toBoolean() : conventionValue
    }

    private static File getFileProperty(Project project, String propertyName, File conventionValue) {
        String projectProperty = getProjectProperty(project, propertyName)
        projectProperty ? new File(projectProperty) : conventionValue
    }
}
