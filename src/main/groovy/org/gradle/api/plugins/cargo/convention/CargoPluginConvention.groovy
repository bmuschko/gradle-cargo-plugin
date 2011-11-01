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
package org.gradle.api.plugins.cargo.convention

/**
 * Defines Cargo convention.
 *
 * @author Benjamin Muschko
 */
class CargoPluginConvention {
    String containerId
    Integer port = 8080
    String context
    File deployable
    CargoRemoteTaskConvention remote = new CargoRemoteTaskConvention()
    CargoLocalTaskConvention local = new CargoLocalTaskConvention()

    def cargo(Closure closure) {
        closure.delegate = this
        closure()
    }

    def remote(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = remote
        closure()
    }

    def local(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = local
        closure()
    }
}
