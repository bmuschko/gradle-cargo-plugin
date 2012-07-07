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

import groovy.util.logging.Slf4j

/**
 * Defines local container task property.
 *
 * @author Benjamin Muschko
 */
@Slf4j
enum LocalContainerTaskProperty implements TaskProperty {
    JVM_ARGS('cargo.jvmargs', PropertyDataType.STRING), LOG_LEVEL('cargo.log.level', PropertyDataType.STRING),
    HOME_DIR('cargo.home.dir', PropertyDataType.FILE), OUTPUT('cargo.output', PropertyDataType.FILE),
    LOG('cargo.log', PropertyDataType.FILE)

    static final Map PROPERTIES

    static {
        PROPERTIES = [:]

        values().each { property ->
            PROPERTIES.put(property.name, property)
        }
    }

    final String name
    final PropertyDataType type

    private LocalContainerTaskProperty(String name, PropertyDataType type) {
        this.name = name
        this.type = type
    }

    static getPropertyForName(name) {
        def property = PROPERTIES[name]

        if(!property) {
            log.error "Unknown property: $name"
            throw new IllegalArgumentException("Unknown property: $name")
        }

        property
    }
}