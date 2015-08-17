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
package com.bmuschko.gradle.cargo.tasks.local

import com.bmuschko.gradle.cargo.Container
import com.bmuschko.gradle.cargo.DeployableType
import com.bmuschko.gradle.cargo.DeployableTypeFactory
import com.bmuschko.gradle.cargo.convention.BinFile
import com.bmuschko.gradle.cargo.convention.ConfigFile
import com.bmuschko.gradle.cargo.convention.Deployable
import com.bmuschko.gradle.cargo.convention.ZipUrlInstaller
import com.bmuschko.gradle.cargo.tasks.AbstractCargoContainerTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*

/**
 * Deploys WAR to local container.
 */
class LocalCargoContainerTask extends AbstractCargoContainerTask {
    /**
     * Level representing the quantity of information we wish to log.
     */
    @Input
    @Optional
    String logLevel

    /**
     * JVM args to be used when starting/stopping containers.
     */
    @Input
    @Optional
    String jvmArgs

    /**
     * The container's installation home directory.
     */
    @Optional
    File homeDir

    /**
     * The Cargo configuration home directory.
     */
    @Optional
    File configHomeDir

    /**
     * The path to a file where container logs are saved.
     */
    @OutputFile
    @Optional
    File outputFile

    /**
     * The path to a file where Cargo logs are saved.
     */
    @OutputFile
    @Optional
    File logFile

    /**
     * The port to use when communicating with this server.
     */
    @Input
    @Optional
    Integer rmiPort

    /**
     * Timeout after which the container start/stop is deemed failed.
     */
    @Input
    @Optional
    Integer timeout

    /**
     * The system properties passed-on the container.
     */
    @Input
    Map<String, Object> systemProperties = [:]

    /**
     * The list of configuration files.
     */
    @Input
    List<ConfigFile> configFiles = []

    /**
     * The list of binary files.
     */
    @Input
    List<BinFile> files = []

    /**
     * Configurable ZIP URL installer instance for automatically downloading a container.
     */
    @Input
    ZipUrlInstaller zipUrlInstaller = new ZipUrlInstaller()

    /**
     * Additional libraries for your application's classpath that are not exposed to the container.
     */
    @InputFiles
    @Optional
    FileCollection sharedClasspath

    /**
     * Additional libraries added to the container's classpath.
     */
    @InputFiles
    @Optional
    FileCollection extraClasspath

    @Override
    void validateConfiguration() {
        super.validateConfiguration()

        if(!getDeployables().isEmpty()) {
            getDeployables().each { deployable ->
                if(deployable.file && !deployable.file.exists()) {
                    throw new InvalidUserDataException("Deployable "
                            + (deployable.file == null ? "null" : deployable.file.canonicalPath)
                            + " does not exist")
                }
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

        ant.taskdef(resource: AbstractCargoContainerTask.CARGO_TASKS, classpath: getClasspath().asPath)
        ant.cargo(getCargoAttributes()) {
            ant.configuration(getConfigurationAttributes()) {
                property(name: AbstractCargoContainerTask.CARGO_SERVLET_PORT, value: getPort())

                if(getJvmArgs()) {
                    ant.property(name: 'cargo.jvmargs', value: getJvmArgs())
                }

                if(getLogLevel()) {
                    ant.property(name: 'cargo.logging', value: getLogLevel())
                }

                if(getRmiPort()) {
                    ant.property(name: 'cargo.rmi.port', value: getRmiPort())
                }

                setContainerSpecificProperties()

                getDeployables().each { Deployable deployable ->
                    DeployableType deployableType = DeployableTypeFactory.instance.getType(deployable)

                    if(deployable.context) {
                        ant.deployable(type: deployableType.type, file: deployable.file) {
                            ant.property(name: AbstractCargoContainerTask.CARGO_CONTEXT, value: deployable.context)
                        }
                    }
                    else {
                        ant.deployable(type: deployableType.type, file: deployable.file)
                    }
                }

                getConfigFiles().each { configFile ->
                    ant.configfile(file: configFile.file, todir: configFile.toDir)
                }

                getFiles().each { binFile ->
                    ant.file(file: binFile.file, todir: binFile.toDir)
                }
            }

            setSystemProperties()

            if(getZipUrlInstaller().isValid()) {
                ant.zipUrlInstaller(installUrl: getZipUrlInstaller().installUrl, downloadDir: getZipUrlInstaller().downloadDir,
                        extractDir: getZipUrlInstaller().extractDir)
            }

            if(getExtraClasspath()) {
                ant.extraClasspath() {
                    getExtraClasspath().addToAntBuilder(ant, 'fileset', FileCollection.AntType.FileSet)
                }
            }

            if(getSharedClasspath()) {
                ant.sharedClasspath() {
                    getSharedClasspath().addToAntBuilder(ant, 'fileset', FileCollection.AntType.FileSet)
                }
            }
        }
    }

    protected Map<String, String> getConfigurationAttributes() {
        def config = [:]

        if(getConfigHomeDir()) {
            if(!getConfigHomeDir().exists()) {
                ant.mkdir(getConfigHomeDir())
            }

            config['home'] = getConfigHomeDir().absolutePath
        }

        return config
    }

    private Map<String, String> getCargoAttributes() {
        def cargoAttributes = ['containerId': getContainerId(), 'action': getAction()]
        if (getTimeout()) {
            cargoAttributes['timeout'] = getTimeout()
        }

        if(!getZipUrlInstaller().isValid()) {
            cargoAttributes['home'] = getHomeDir().canonicalPath
        }

        if(getOutputFile()) {
            cargoAttributes['output'] = getOutputFile()
        }

        if(getLogFile()) {
            cargoAttributes['log'] = getLogFile()
        }

        cargoAttributes
    }

    void setSystemProperties() {
        logger.info "System properties = ${getSystemProperties()}"

        getSystemProperties().each { key, value ->
            ant.sysproperty(key: key, value: value)
        }
    }
}
