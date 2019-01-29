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

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles

/**
 * Defines ConfigFile convention.
 */
class ConfigFile {

    /**
     * This specifies the files that should be copied over.
     * Can also specify directories if whole directories need to be copied over.
     */
    @InputFiles
    FileCollection files

    /**
     * This specified the name the directory that the file should be copied to relative to the configurations home.
     */
    @Input
    String toDir
}
