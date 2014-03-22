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
package org.gradle.api.plugins.cargo.convention

import org.gradle.api.file.FileCollection
import org.gradle.util.ConfigureUtil

/**
 * Defines Cargo local task convention.
 *
 * @author Benjamin Muschko
 */
class CargoLocalTaskConvention {
    String jvmArgs
    String logLevel
    File homeDir
    File configHomeDir
    File outputFile
    File log
    Integer rmiPort
    ZipUrlInstaller zipUrlInstaller = new ZipUrlInstaller()
    def configFiles = []
    def files = []
    ContainerProperties containerProperties = new ContainerProperties()
    SystemProperties systemProperties = new SystemProperties()
    FileCollection extraClasspath
    FileCollection sharedClasspath

    def installer(Closure closure) {
        ConfigureUtil.configure(closure, zipUrlInstaller)
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
        ConfigureUtil.configure(closure, containerProperties)
    }

    def systemProperties(Closure closure) {
        ConfigureUtil.configure(closure, systemProperties)
    }
}
