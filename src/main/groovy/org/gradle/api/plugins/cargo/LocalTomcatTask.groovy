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

import org.gradle.api.plugins.cargo.property.LocalTomcatTaskProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional

/**
 * Deploys WAR to local Tomcat container.
 *
 * @author Benjamin Muschko
 */
class LocalTomcatTask extends LocalContainerTask {
    @InputDirectory @Optional File webappsDir
    Boolean copyWars
    Boolean contextReloadable
    Integer ajpPort

    @Override
    void setContainerSpecificProperties() {
        if(getWebappsDir()) {
            property(name: LocalTomcatTaskProperty.WEBAPPS_DIRECTORY, value: getWebappsDir())
        }
        if(getCopyWars()) {
            property(name: LocalTomcatTaskProperty.COPY_WARS, value: getCopyWars())
        }
        if(getContextReloadable()) {
            property(name: LocalTomcatTaskProperty.CONTEXT_RELOADABLE, value: getContextReloadable())
        }
        if(getAjpPort()) {
            property(name: LocalTomcatTaskProperty.AJP_PORT, value: getAjpPort())
        }
    }
}
