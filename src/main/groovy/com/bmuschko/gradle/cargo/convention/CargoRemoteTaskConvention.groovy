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
package com.bmuschko.gradle.cargo.convention

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

/**
 * Defines Cargo remote task convention.
 */
class CargoRemoteTaskConvention {
    final Property<String> protocol
    final Property<String> hostname
    final Property<String> username
    final Property<String> password
    final MapProperty<String, Object> containerProperties

    CargoRemoteTaskConvention(ObjectFactory objectFactory) {
        protocol = objectFactory.property(String)
        protocol.set("http")

        hostname = objectFactory.property(String)
        username = objectFactory.property(String)
        password = objectFactory.property(String)
        containerProperties = objectFactory.mapProperty(String, Object)
    }

    def containerProperties(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = new MapPropertyExtension(containerProperties)
        closure()
    }
}
