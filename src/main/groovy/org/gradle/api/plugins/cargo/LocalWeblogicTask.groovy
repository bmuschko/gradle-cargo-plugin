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

import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.plugins.cargo.property.LocalWeblogicTaskProperty

/**
 * Deploys WAR to local Weblogic container.
 *
 * @author Benjamin Muschko
 */
class LocalWeblogicTask extends LocalContainerTask {
    String adminUser
    String adminPassword
    @InputDirectory @Optional File beaHome
    String server

    @Override
    void setContainerSpecificProperties() {
        if(getAdminUser()) {
            property(name: LocalWeblogicTaskProperty.ADMIN_USER, value: getAdminUser())
        }
        if(getAdminPassword()) {
            property(name: LocalWeblogicTaskProperty.ADMIN_PASSWORD, value: getAdminPassword())
        }
        if(getBeaHome()) {
            property(name: LocalWeblogicTaskProperty.BEA_HOME, value: getBeaHome())
        }
        if(getServer()) {
            property(name: LocalWeblogicTaskProperty.SERVER, value: getServer())
        }
    }
}
