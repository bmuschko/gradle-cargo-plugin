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
package com.bmuschko.gradle.cargo.convention

/**
 * Defines Deployable convention.
 */
class ConfigFile implements Serializable {

    /**
     * This specifies the path to the file that should be used.
     * Can also specify a directory path if a whole directory needs to be copied over.
     */
    File file

    /**
     * This specified the name the directory that the file should be copied to relative to the configurations home.
     */
    String toDir

    @Override
    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ConfigFile that = (ConfigFile) o

        if (file != that.file) return false
        if (toDir != that.toDir) return false

        return true
    }

    @Override
    int hashCode() {
        int result
        result = (file != null ? file.hashCode() : 0)
        result = 31 * result + (toDir != null ? toDir.hashCode() : 0)
        return result
    }
}
