/*
 * Copyright 2012 the original author or authors.
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
 * Defines a binary file to be used in a local container.
 *
 * @author Sascha Kiedrowski
 */
class BinFile implements Serializable {
    File file
    File toDir
    
    boolean equals(obj) {
        if (obj == null) {
            return false
        } else if (!(obj instanceof BinFile)) {
            return false
        } else {
            BinFile that = obj
            return that.file == this.file && that.toDir == this.toDir
        }
    }
}
