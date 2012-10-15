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
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional

/**
 * Deploys WAR to local Tomcat container.
 *
 * @author Benjamin Muschko
 */
class LocalTomcatTask extends LocalContainerTask {
    @InputDirectory @Optional File webappsDir
    @Input @Optional Boolean copyWars
    @Input @Optional Boolean contextReloadable
    @Input @Optional Integer ajpPort
    @Input @Optional Integer rmiPort

    @Override
    void setContainerSpecificProperties() {
        if(getWebappsDir()) {
            ant.property(name: LocalTomcatTaskProperty.WEBAPPS_DIRECTORY.name, value: getWebappsDir())
        }
        if(getCopyWars()) {
            ant.property(name: LocalTomcatTaskProperty.COPY_WARS.name, value: getCopyWars())
        }
        if(getContextReloadable()) {
            ant.property(name: LocalTomcatTaskProperty.CONTEXT_RELOADABLE.name, value: getContextReloadable())
        }
        if(getAjpPort()) {
            ant.property(name: LocalTomcatTaskProperty.AJP_PORT.name, value: getAjpPort())
        }
        if(getRmiPort()) {
            ant.property(name: LocalTomcatTaskProperty.RMI_PORT.name, value: getRmiPort())
        }
    }
}
