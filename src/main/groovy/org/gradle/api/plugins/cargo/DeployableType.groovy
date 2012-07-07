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

/**
 * Type of deployable.
 *
 * @author Benjamin Muschko
 */
@Slf4j
enum DeployableType {
    WAR('war'), EAR('ear')

    static final Map DEPLOYABLE_TYPES

    static {
        DEPLOYABLE_TYPES = [:]

        values().each { DeployableType deployableType ->
            DEPLOYABLE_TYPES.put(deployableType.filenameExtension, deployableType)
        }
    }

    final String filenameExtension

    DeployableType(String filenameExtension) {
        this.filenameExtension = filenameExtension
    }

    static DeployableType getDeployableTypeForFilenameExtension(String filenameExtension) {
        DeployableType deployableType = DEPLOYABLE_TYPES[filenameExtension]

        if(!deployableType) {
            log.error "Unknown deployable type: ${filenameExtension}"
            throw new IllegalArgumentException('Unknown deployable type')
        }

        deployableType
    }
}
