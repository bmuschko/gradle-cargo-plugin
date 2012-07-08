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

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional

/**
 * Deploys WAR to local container.
 *
 * @author Benjamin Muschko
 */
@Slf4j
class LocalContainerTask extends AbstractContainerTask {
    String logLevel
    String jvmArgs
    @InputDirectory @Optional File homeDir
    File output
    File logFile
    ZipUrlInstaller zipUrlInstaller

    @Override
    void runAction() {
        log.info "Starting action '${getAction()}' for local container '${Container.getContainerForId(getContainerId()).description}'"

        ant.taskdef(resource: CARGO_TASKS, classpath: getClasspath().asPath)
        ant.cargo(getCargoAttributes()) {
            ant.configuration {
                property(name: CARGO_SERVLET_PORT, value: getPort())

                if(getJvmArgs()) {
                    ant.property(name: 'cargo.jvmargs', value: getJvmArgs())
                }

                if(getLogLevel()) {
                    ant.property(name: 'cargo.logging', value: getLogLevel())
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

                setContainerSpecificProperties()
            }

            if(getZipUrlInstaller().isValid()) {
                ant.zipUrlInstaller(installUrl: getZipUrlInstaller().installUrl, downloadDir: getZipUrlInstaller().downloadDir,
                        extractDir: getZipUrlInstaller().extractDir)
            }
        }
    }

    private Map<String, String> getCargoAttributes() {
        def cargoAttributes = ['containerId': getContainerId(), 'action': getAction()]

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
