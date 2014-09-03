/*
 * Copyright 2013 the original author or authors.
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
package org.gradle.api.plugins.cargo.tasks.local

/**
 * Starts the local container, deploys web application to it and wait for the user to press CTRL + C to stop.
 *
 * @author Benjamin Muschko
 */
class CargoRunLocal extends LocalCargoContainerTask {
    CargoRunLocal() {
        action = 'run'
        description = 'Starts the container, deploys WAR to it and wait for the user to press CTRL + C to stop.'
    }
}
