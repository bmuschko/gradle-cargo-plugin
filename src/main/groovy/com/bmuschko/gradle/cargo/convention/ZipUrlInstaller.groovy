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
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory

import java.util.function.Supplier

/**
 * ZIP URL installer properties.
 *
 * @see <a href="http://cargo.codehaus.org/Installer">Installer documentation</a>
 */
class ZipUrlInstaller {

    private Supplier<String> installUrlSupplier = { null }

    @Input
    @Optional
    String installUrl

    @InputFiles
    @Optional
    FileCollection installConfiguration

    @OutputDirectory
    File downloadDir

    @OutputDirectory
    File extractDir

    @Internal
    String getConfiguredInstallUrl() {
        installUrlSupplier.get()
    }

    @Internal
    boolean isValid() {
        configuredInstallUrl && downloadDir && extractDir
    }

    void setInstallUrl(String installUrl) {
        this.installUrl = installUrl
        this.installConfiguration = null
        installUrlSupplier = { installUrl }
    }

    void setInstallConfiguration(FileCollection configuration) {
        this.installUrl = null
        this.installConfiguration = configuration
        installUrlSupplier = { configuration.singleFile.toURI().toURL().toString() }
    }

}
