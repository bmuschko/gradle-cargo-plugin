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

import org.apache.tools.ant.AntClassLoader
import org.gradle.api.DefaultTask
import org.gradle.api.UncheckedIOException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

import java.lang.reflect.Constructor

abstract class CargoDaemon extends DefaultTask {
    @InputFiles
    FileCollection classpath

    /**
     * Protocol on which the container is listening to. Defaults to 'http'.
     */
    @Input
    String protocol = 'http'

    /**
     * Host name on which the container listens to. Defaults to 'localhost'.
     */
    @Input
    String hostname = 'localhost'

    /**
     * Port on which the daemon listens to.
     */
    @Input
    Integer port = 18000

    /**
     * Username to use to authenticate against a remote container.
     */
    @Input
    @Optional
    String username

    /**
     * Password to use to authenticate against a remote container.
     */
    @Input
    @Optional
    String password

    /**
     * The handle ID identifying a specific container configuration.
     */
    @Input
    String handleId

    @TaskAction
    void messageDaemon() {
        withCargoDaemonClassLoader { URLClassLoader classLoader ->
            def daemonClient = createDaemonClient(classLoader)
            performDaemonOperation(daemonClient, classLoader)
        }
    }

    def createDaemonClient(URLClassLoader classLoader) {
        URL daemonURL = new URL("${getProtocol()}://${getHostname()}:${getPort()}")
        Class daemonClientClass = classLoader.loadClass('org.codehaus.cargo.tools.daemon.DaemonClient')
        def daemonClient

        if(!isBlank(getUsername()) && !isBlank(getPassword())) {
            Constructor daemonClientConstructor = daemonClientClass.getConstructor(URL, String, String)
            daemonClient = daemonClientConstructor.newInstance(daemonURL, getUsername(), getPassword())
        }
        else if(!isBlank(getUsername())) {
            Constructor daemonClientConstructor = daemonClientClass.getConstructor(URL, String)
            daemonClient = daemonClientConstructor.newInstance(daemonURL, getUsername())
        }
        else {
            Constructor daemonClientConstructor = daemonClientClass.getConstructor(URL)
            daemonClient = daemonClientConstructor.newInstance(daemonURL)
        }

        daemonClient
    }

    private boolean isBlank(String parameter) {
        parameter != null && parameter.length() > 0
    }

    abstract void performDaemonOperation(daemonClient, URLClassLoader classLoader)

    private void withCargoDaemonClassLoader(Closure c) {
        ClassLoader originalClassLoader = getClass().classLoader
        URLClassLoader cargoDaemonClassloader = createCargoDaemonClassLoader()

        try {
            Thread.currentThread().contextClassLoader = cargoDaemonClassloader
            c(cargoDaemonClassloader)
        }
        finally {
            Thread.currentThread().contextClassLoader = originalClassLoader
        }
    }

    private URLClassLoader createCargoDaemonClassLoader() {
        ClassLoader rootClassLoader = new AntClassLoader(getClass().classLoader, false)
        new URLClassLoader(toURLArray(getClasspath().files), rootClassLoader)
    }

    private URL[] toURLArray(Collection<File> files) {
        List<URL> urls = new ArrayList<URL>(files.size())

        for(File file : files) {
            try {
                urls.add(file.toURI().toURL())
            }
            catch(MalformedURLException e) {
                throw new UncheckedIOException(e)
            }
        }

        urls.toArray(new URL[urls.size()]);
    }
}
