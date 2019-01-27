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

import org.gradle.api.file.FileCollection

/**
 * Defines Cargo local task convention.
 */
class CargoLocalTaskConvention {

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

    def installer(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = zipUrlInstaller
        closure()
    }

    def configFile(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        ConfigFile configFile = new ConfigFile()
        closure.delegate = configFile
        configFiles << configFile
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
}
