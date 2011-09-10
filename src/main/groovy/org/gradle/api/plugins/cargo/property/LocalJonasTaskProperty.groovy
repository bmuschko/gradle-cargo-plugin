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
package org.gradle.api.plugins.cargo.property

import org.slf4j.LoggerFactory
import org.slf4j.Logger

/**
 * Defines local Jonas task convention.
 *
 * @author Benjamin Muschko
 */
enum LocalJonasTaskProperty {
    JMS_PORT('cargo.jonas.jms.port', PropertyDataType.INTEGER), SERVER_NAME('cargo.jonas.server.name', PropertyDataType.STRING),
    SERVICES_LIST('cargo.jonas.services.list', PropertyDataType.STRING), DOMAIN_NAME('cargo.jonas.domain.name', PropertyDataType.STRING)

    static final Logger LOGGER = LoggerFactory.getLogger(LocalJonasTaskProperty)
    static final Map PROPERTIES

    static {
        PROPERTIES = [:]

        values().each { property ->
            PROPERTIES.put(property.name, property)
        }
    }

    final String name
    final PropertyDataType type

    private LocalJonasTaskProperty(String name, PropertyDataType type) {
        this.name = name
        this.type = type
    }

    static getPropertyForName(name) {
        def property = PROPERTIES[name]

        if(!property) {
            LOGGER.error "Unknown property: $name"
            throw new IllegalArgumentException("Unknown property: $name")
        }

        property
    }
}