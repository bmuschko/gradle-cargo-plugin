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
package com.bmuschko.gradle.cargo.util

/**
 * Filename utilities.
 */
class DefaultFileUtil implements FileUtil {
    @Override
    String getExtension(File file) {
        if(!file.exists()) {
            throw new IllegalArgumentException("File '$file.canonicalPath' does not exist")
        }

        if(file.isDirectory()) {
            return ''
        }

        String filename = file.canonicalPath
        int index = filename.lastIndexOf('.')

        if (index > 0) {
            return filename.substring(index + 1)
        }

        ''
    }
}
