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
package org.gradle.api.plugins.cargo

import org.gradle.api.InvalidUserDataException
import org.gradle.api.plugins.cargo.convention.BinFile
import org.gradle.api.plugins.cargo.convention.ConfigFile
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional

/**
 * Deploys WAR to local container.
 *
 * @author Benjamin Muschko
 */
class LocalContainerTask extends AbstractContainerTask {
    String logLevel
    String jvmArgs
    @InputDirectory @Optional File homeDir
    File configHomeDir
    File output
    File logFile
    @Input @Optional Integer rmiPort
    ZipUrlInstaller zipUrlInstaller
    List<ConfigFile> configFiles
    List<BinFile> files

    @Override
    void validateConfiguration() {
        super.validateConfiguration()

        getDeployables().each { deployable ->
            if(deployable.file && !deployable.file.exists()) {
                throw new InvalidUserDataException("Deployable "
                        + (deployable.file == null ? "null" : deployable.file.canonicalPath)
                        + " does not exist")
            }

            logger.info "Deployable artifacts = ${getDeployables().collect { it.file.canonicalPath }}"
        }

        if(!getConfigFiles().isEmpty()) {
            getConfigFiles().each { configFile ->
                if(!configFile.file || !configFile.file.exists()) {
                    throw new InvalidUserDataException("Config file "
                    + (configFile.file == null ? "null" : configFile.file.canonicalPath)
                    + " does not exist")
                }
            }

            logger.info "Config files = ${getConfigFiles().collect { it.file.canonicalPath + " -> " + it.toDir.canonicalPath }}"
        }

        if (!getFiles().isEmpty()) {
            getFiles().each { binFile ->
                if (!binFile.file || !binFile.file.exists()) {
                    throw new InvalidUserDataException("Binary File "
                    + (binFile.file == null ? "null" : binFile.file.canonicalPath)
                    + " does not exist")
                }
            }
            logger.info "Binary files = ${getFiles().collect { it.file.canonicalPath + " -> " + it.toDir.canonicalPath }}"
        }
    }

    @Override
    void runAction() {
        logger.info "Starting action '${getAction()}' for local container '${Container.getContainerForId(getContainerId()).description}'"

        ant.taskdef(resource: CARGO_TASKS, classpath: getClasspath().asPath)
        ant.cargo(getCargoAttributes()) {
            ant.configuration(getConfigurationAttributes()) {
                property(name: CARGO_SERVLET_PORT, value: getPort())

                if(getJvmArgs()) {
                    ant.property(name: 'cargo.jvmargs', value: getJvmArgs())
                }

                if(getLogLevel()) {
                    ant.property(name: 'cargo.logging', value: getLogLevel())
                }

                if(getRmiPort()) {
                    ant.property(name: 'cargo.rmi.port', value: getRmiPort())
                }

                getDeployables().each { deployable ->
                    if(deployable.context) {
                        ant.deployable(type: getDeployableType(deployable).filenameExtension, file: deployable.file) {
                            ant.property(name: CARGO_CONTEXT, value: deployable.context)
                        }
                    }
                    else {
                        ant.deployable(type: getDeployableType(deployable).filenameExtension, file: deployable.file)
                    }
                }

                getConfigFiles().each { configFile ->
                    ant.configfile(file: configFile.file, todir: configFile.toDir)
                }

                getFiles().each { binFile ->
                    ant.file(file: binFile.file, todir: binFile.toDir)
                }

                setContainerSpecificProperties()
            }

            if(getZipUrlInstaller().isValid()) {
                ant.zipUrlInstaller(installUrl: getZipUrlInstaller().installUrl, downloadDir: getZipUrlInstaller().downloadDir,
                        extractDir: getZipUrlInstaller().extractDir)
            }
        }
    }

    protected Map<String, String> getConfigurationAttributes() {
        def config = [:]

        if(getConfigHomeDir()) {
            config['home'] = getConfigHomeDir().absolutePath
        }

        return config
    }

    private Map<String, String> getCargoAttributes() {
        def cargoAttributes = ['containerId': getContainerId(), 'action': getAction(), 'timeout': getTimeout()]

        if(!getZipUrlInstaller().isValid()) {
            cargoAttributes['home'] = getHomeDir().canonicalPath
        }

        if(getOutput()) {
            cargoAttributes['output'] = getOutput()
        }

        if(getLogFile()) {
            cargoAttributes['log'] = getLogFile()
        }

        cargoAttributes
    }

    void setContainerSpecificProperties() {}
}
