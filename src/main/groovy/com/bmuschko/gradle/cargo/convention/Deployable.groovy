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

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional

/**
 * Defines Deployable convention.
 */
class Deployable implements Serializable {
    @InputFile
    @Optional
    Property<File> file

    @InputFiles
    @Optional
    ConfigurableFileCollection files

    @Input
    @Optional
    Property<String> context

    private final Project project

    @javax.inject.Inject
    Deployable(Project project) {
        this.project = project
        file = project.objects.property(File)
        files = project.layout.configurableFiles()
        context = project.objects.property(String)
    }

    void setConfiguration(FileCollection files) {
        this.files.setFrom(files)
        Provider<File> fileProvider = project.providers.provider({this.files.singleFile})
        file.set(fileProvider)
    }

    void setFile(Provider<File> file) {
        this.file.set(file)
    }

    void setFile(File file) {
        this.file.set(file)
    }

    void setContext(String context) {
        this.context.set(context)
    }
}
