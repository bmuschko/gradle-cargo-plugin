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

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

/**
 * Retireves Cargo project property.
 *
 * @author Benjamin Muschko
 */
class CargoProjectProperty {
    static final CARGO_HOME_DIR_PROJECT_PROPERTY = 'cargo.home.dir'
    static final CARGO_CONTAINER_ID_PROJECT_PROPERTY = 'cargo.container.id'
    static final CARGO_PROTOCOL_PROJECT_PROPERTY = 'cargo.protocol'
    static final CARGO_HOSTNAME_PROJECT_PROPERTY = 'cargo.hostname'
    static final CARGO_PORT_PROJECT_PROPERTY = 'cargo.port'
    static final CARGO_TOMCAT_AJP_PORT_PROJECT_PROPERTY = 'cargo.tomcat.ajp.port'
    static final CARGO_USERNAME_PROJECT_PROPERTY = 'cargo.username'
    static final CARGO_PASSWORD_PROJECT_PROPERTY = 'cargo.password'
    static final CARGO_CONTEXT_PROJECT_PROPERTY = 'cargo.context'
    static final CARGO_WAIT_PROJECT_PROPERTY = 'cargo.wait'
    static final CARGO_LOGGING_PROJECT_PROPERTY = 'cargo.log.level'

    static String getLogLevel(Project project, CargoPluginConvention cargoConvention) {
        String projectProperty = getProjectProperty(project, CARGO_LOGGING_PROJECT_PROPERTY)
        projectProperty ?: cargoConvention.local.logLevel
    }

    static File getHomeDir(Project project, CargoPluginConvention cargoConvention) {
        String projectProperty = getProjectProperty(project, CARGO_HOME_DIR_PROJECT_PROPERTY)
        projectProperty ? new File(projectProperty) : cargoConvention.local.homeDir
    }

    static String getContainerId(Project project, CargoPluginConvention cargoConvention) {
        String projectProperty = getProjectProperty(project, CARGO_CONTAINER_ID_PROJECT_PROPERTY)
        projectProperty ?: cargoConvention.containerId
    }

    static String getProtocol(Project project, CargoPluginConvention cargoConvention) {
        String projectProperty = getProjectProperty(project, CARGO_PROTOCOL_PROJECT_PROPERTY)
        projectProperty ?: cargoConvention.remote.protocol
    }

    static String getHostname(Project project, CargoPluginConvention cargoConvention) {
        String projectProperty = getProjectProperty(project, CARGO_HOSTNAME_PROJECT_PROPERTY)
        projectProperty ?: cargoConvention.remote.hostname
    }

    static Integer getPort(Project project, CargoPluginConvention cargoConvention) {
        String projectProperty = getProjectProperty(project, CARGO_PORT_PROJECT_PROPERTY)

        if(projectProperty) {
            try {
                return projectProperty.toInteger()
            }
            catch(NumberFormatException e) {
                throw new InvalidUserDataException("Bad port provided as project property: '$projectProperty'", e)
            }
        }

        cargoConvention.port
    }

    static Integer getTomcatAjpPort(Project project, CargoPluginConvention cargoConvention) {
        String projectProperty = getProjectProperty(project, CARGO_TOMCAT_AJP_PORT_PROJECT_PROPERTY)

        if(projectProperty) {
            try {
                return projectProperty.toInteger()
            }
            catch(NumberFormatException e) {
                throw new InvalidUserDataException("Bad Tomcat AJP port provided as project property: '$projectProperty'", e)
            }
        }

        cargoConvention.tomcatAjpPort
    }

    static String getUsername(Project project, CargoPluginConvention cargoConvention) {
        String projectProperty = getProjectProperty(project, CARGO_USERNAME_PROJECT_PROPERTY)
        projectProperty ?: cargoConvention.remote.username
    }

    static String getPassword(Project project, CargoPluginConvention cargoConvention) {
        String projectProperty = getProjectProperty(project, CARGO_PASSWORD_PROJECT_PROPERTY)
        projectProperty ?: cargoConvention.remote.password
    }

    static String getContext(Project project, CargoPluginConvention cargoConvention) {
        String projectProperty = getProjectProperty(project, CARGO_CONTEXT_PROJECT_PROPERTY)
        projectProperty ?: cargoConvention.context
    }

    static Boolean getWait(Project project, CargoPluginConvention cargoConvention) {
        String projectProperty = getProjectProperty(project, CARGO_WAIT_PROJECT_PROPERTY)
        projectProperty ? projectProperty.toBoolean() : cargoConvention.wait
    }

    private static String getProjectProperty(Project project, String name) {
        project.hasProperty(name) ? project.property(name) : null
    }
}
