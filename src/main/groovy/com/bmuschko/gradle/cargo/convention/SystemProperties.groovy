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
package com.bmuschko.gradle.cargo.convention

/**
 * Properties that can be passed on to the local container.
 *
 * @author Benjamin Muschko
 */
class SystemProperties {
    Map<String, Object> properties = [:]

    void property(String key, Object value) {
        properties[key] = value
    }

    void properties(Map<String, ?> properties) {
        this.properties.putAll(properties)
    }
}
