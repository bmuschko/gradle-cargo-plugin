/*
 * Copyright 2014 the original author or authors.
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
package com.bmuschko.gradle.cargo.tasks.daemon
/**
 * Starts the configured container via the Cargo daemon process.
 *
 * @author Benjamin Muschko
 */
class CargoDaemonStart extends CargoDaemon {
    @Override
    void performDaemonOperation(daemonClient, URLClassLoader classLoader) {
        logger.info "Starting container with handle ID '${getHandleId()}' over daemon URL '${daemonClient.getURL()}'."
        Class daemonStartClass = classLoader.loadClass('org.codehaus.cargo.tools.daemon.DaemonStart')
        def request = daemonStartClass.newInstance()
        request.handleId = getHandleId()
        daemonClient.start(request)
    }
}
