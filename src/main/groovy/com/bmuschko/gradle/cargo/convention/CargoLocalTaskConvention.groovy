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
import org.gradle.api.file.FileCollection

/**
 * Defines Cargo local task convention.
 */
class CargoLocalTaskConvention {

    private final Project project

    String jvmArgs
    String logLevel
    File homeDir
    String configType
    File configHomeDir
    File outputFile
    File logFile
    Integer rmiPort
    ZipUrlInstaller zipUrlInstaller = new ZipUrlInstaller()
    def configFiles = []
    def files = []
    ContainerProperties containerProperties = new ContainerProperties()
    SystemProperties systemProperties = new SystemProperties()
    FileCollection extraClasspath
    FileCollection sharedClasspath

    CargoLocalTaskConvention(Project project) {
        this.project = project
    }

    def installer(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = zipUrlInstaller
        closure()
    }

    def configFile(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        ConfigFileClosureDelegate configFileClosureDelegate = new ConfigFileClosureDelegate(project)
        closure.delegate = configFileClosureDelegate
        configFiles << configFileClosureDelegate.configFile
        closure()
    }

    def file(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        BinFile file = new BinFile()
        closure.delegate = file
        files << file
        closure()
    }

    def containerProperties(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = containerProperties
        closure()
    }

    def systemProperties(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = systemProperties
        closure()
    }

    private static class ConfigFileClosureDelegate {

        @Delegate
        final ConfigFile configFile = new ConfigFile()

        private final Project project

        ConfigFileClosureDelegate(Project project) {
            this.project = project
        }

        void setFile(Object file) {
            configFile.files = project.files(file)
        }

        void setFiles(Object file) {
            configFile.files = project.files(file)
        }
    }
}
