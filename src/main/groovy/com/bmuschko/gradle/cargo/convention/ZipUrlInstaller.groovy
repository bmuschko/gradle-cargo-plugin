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

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory


/**
 * ZIP URL installer properties.
 *
 * @see <a href="http://cargo.codehaus.org/Installer">Installer documentation</a>
 */
class ZipUrlInstaller {
    private final String DOWNLOAD_DIR = "javaee/download"
    private final String EXTRACT_DIR = "javaee/home"

    // Object type, it will be given to ant.Zipurlinstaller
    private Property<Object> installUrlSupplier

    @Input
    @Optional
    Property<String> installUrl

    @InputFiles
    @Optional
    ConfigurableFileCollection installConfiguration

    @OutputDirectory
    DirectoryProperty downloadDir

    @OutputDirectory
    DirectoryProperty extractDir
    private final Project project

    ZipUrlInstaller(Project project) {
        this.project = project
        extractDir = project.objects.directoryProperty()
        extractDir.convention(project.layout.buildDirectory.dir(EXTRACT_DIR))

        downloadDir = project.objects.directoryProperty()
        downloadDir.convention(project.layout.buildDirectory.dir(DOWNLOAD_DIR))

        installUrlSupplier = project.objects.property(Object)
        installUrl = project.objects.property(String)
        installConfiguration = project.layout.configurableFiles()
    }

    void setDownloadDir(File file) {
        downloadDir.set(file)
    }

    void setExtractDir(File file) {
        extractDir.set(file)
    }

    @Internal
    Provider<Object> getConfiguredInstallUrl() {
        installUrlSupplier
    }

    @Internal
    boolean isValid() {
        configuredInstallUrl.present && downloadDir.present && extractDir.present
    }

    void setInstallUrl(String installUrl) {
        this.installUrl.set(installUrl)
        installUrlSupplier.set(this.installUrl) // point to installUrl
    }

    void setInstallConfiguration(FileCollection configuration) {
        this.installConfiguration.setFrom(configuration)
        installUrlSupplier.set(project.providers.provider({installConfiguration.singleFile.toURI()}))
    }

}
