# Gradle Cargo plugin

![Cargo Logo](http://docs.codehaus.org/download/attachments/27913/cargo-banner-left.png)

The plugin provides deployment capabilities for web applications to local and remote containers in any given
Gradle build by leveraging the [Cargo Ant tasks](http://cargo.codehaus.org/Ant+support). The plugin supports WAR and EAR
artifacts.

The typical use case for this plugin is to support deployment during development. Keep in mind that Cargo uses hot deployment
which over time fills up the PermGen memory of the JVM process running your container. Continuously deploying an artifact will
inevitablity lead to a `java.lang.OutOfMemoryError`. Cargo does support container management capabilities (starting/stopping
of remote containers) via the so-called [Cargo daemon](http://cargo.codehaus.org/Cargo+Daemon). However, in continuous deployment
scenarios you often want to need perform more complex operations.

## Usage

To use the plugin's functionality, you will need to add the its binary artifact to your build script's classpath and apply
the plugin.

### Adding the plugin binary to the build

The plugin JAR needs to be defined in the classpath of your build script. It is directly available on
[Bintray](https://bintray.com/bmuschko/gradle-plugins/com.bmuschko%3Agradle-cargo-plugin/). The following code snippet 
shows an example on how to retrieve it from Bintray:

    buildscript {
        repositories {
            jcenter()
        }

        dependencies {
            classpath 'com.bmuschko:gradle-cargo-plugin:2.0.3'
        }
    }

### Provided plugins

The JAR file comes with two plugins:

<table>
    <tr>
        <th>Plugin Identifier</th>
        <th>Depends On</th>
        <th>Type</th>
        <th>Description</th>
    </tr>
    <tr>
        <td>com.bmuschko.cargo-base</td>
        <td>-</td>
        <td><a href="http://bmuschko.github.io/gradle-cargo-plugin/docs/groovydoc/com/bmuschko/gradle/cargo/CargoBasePlugin.html">CargoBasePlugin</a></td>
        <td>Provides Cargo custom task types, pre-configures classpath and deployables.</td>
    </tr>
    <tr>
        <td>com.bmuschko.cargo</td>
        <td>com.bmuschko.cargo-base</td>
        <td><a href="http://bmuschko.github.io/gradle-cargo-plugin/docs/groovydoc/com/bmuschko/gradle/cargo/CargoPlugin.html">CargoPlugin</a></td>
        <td>Provides a set of local and remote Cargo tasks and exposes extension for configuration.</td>
    </tr>
</table>

The `com.bmuschko.cargo` plugin helps you get started quickly. If you only need to deal with a single container product, this is the
preferrable option. Most plugin users will go with this option. To use the Cargo plugin, include the following code snippet
in your build script:

    apply plugin: 'com.bmuschko.cargo'

If you need full control over your deployment tasks, you will want to use the `com.bmuschko.cargo-base` plugin. The downside is that each task
has to be configured individually in your build script. To use the Cargo base plugin, include the following code snippet
in your build script:

    apply plugin: 'com.bmuschko.cargo-base'

### Configuring the Cargo version

The `com.bmuschko.cargo-base` plugin already sets up the dependencies for Cargo. In order to do so, it chooses a default
version of the libraries. Alternatively, you can define a custom version of the Cargo libraries. To do so, please use
the `cargo` configuration name in your `dependencies` closure. Remote deployment functionality will only work with a Cargo
version >= 1.1.0 due to a bug in the library. Please see [CARGO-962](https://jira.codehaus.org/browse/CARGO-962) for more information.
The following example demonstrates how to use the version 1.4.5 of the Cargo libraries:

    dependencies {
        def cargoVersion = '1.4.5'
        cargo "org.codehaus.cargo:cargo-core-uberjar:$cargoVersion",
              "org.codehaus.cargo:cargo-ant:$cargoVersion"
    }

## Tasks

The `cargo` plugin pre-defines the following tasks out-of-the-box:

<table>
    <tr>
        <th>Task Name</th>
        <th>Depends On</th>
        <th>Type</th>
        <th>Description</th>
    </tr>
    <tr>
        <td>cargoDeployRemote</td>
        <td>-</td>
        <td><a href="http://bmuschko.github.io/gradle-cargo-plugin/docs/groovydoc/com/bmuschko/gradle/cargo/tasks/remote/CargoDeployRemote.html">CargoDeployRemote</a></td>
        <td>Deploys web application to remote container.</td>
    </tr>
    <tr>
        <td>cargoUndeployRemote</td>
        <td>-</td>
        <td><a href="http://bmuschko.github.io/gradle-cargo-plugin/docs/groovydoc/com/bmuschko/gradle/cargo/tasks/remote/CargoUndeployRemote.html">CargoUndeployRemote</a></td>
        <td>Undeploys a web application from remote container.</td>
    </tr>
    <tr>
        <td>cargoRedeployRemote</td>
        <td>-</td>
        <td><a href="http://bmuschko.github.io/gradle-cargo-plugin/docs/groovydoc/com/bmuschko/gradle/cargo/tasks/remote/CargoRedeployRemote.html">CargoRedeployRemote</a></td>
        <td>Redeploys web application to remote container.</td>
    </tr>
    <tr>
        <td>cargoRunLocal</td>
        <td>-</td>
        <td><a href="http://bmuschko.github.io/gradle-cargo-plugin/docs/groovydoc/com/bmuschko/gradle/cargo/tasks/local/CargoRunLocal.html">CargoRunLocal</a></td>
        <td>Starts the local container, deploys web application to it and wait for the user to press CTRL + C to stop.</td>
    </tr>
    <tr>
        <td>cargoStartLocal</td>
        <td>-</td>
        <td><a href="http://bmuschko.github.io/gradle-cargo-plugin/docs/groovydoc/com/bmuschko/gradle/cargo/tasks/local/CargoStartLocal.html">CargoStartLocal</a></td>
        <td>Starts the local container, deploys web application to it and then do other tasks (for example, execute tests).</td>
    </tr>
    <tr>
        <td>cargoStopLocal</td>
        <td>-</td>
        <td><a href="http://bmuschko.github.io/gradle-cargo-plugin/docs/groovydoc/com/bmuschko/gradle/cargo/tasks/local/CargoStopLocal.html">CargoStopLocal</a></td>
        <td>Stops local container.</td>
    </tr>
</table>

## Project layout

The Cargo plugin uses the same layout as the War plugin.

## Extension properties

The Cargo plugin defines the following convention properties in the `cargo` closure:

* `containerId`: The container ID you are targeting. Please see the [list of supported containers](http://cargo.codehaus.org/Home) on the Cargo website.
* `port`: The TCP port the container responds on (defaults to 8080).

Within `cargo` you can define optional properties for the 1..n deployment artifacts in a closure named `deployable`. Each
deployment artifact would be specified in its own closure:

* `file`: An arbitrary artifact to be deployed to container (defaults to project/module artifact - WAR or EAR file).
* `context`: The URL context the container is handling your web application on (defaults to WAR/EAR name).

Keep in mind that you do not have to define the `deployable` closure if you just want to deploy the artifact defined by your
Gradle project/module.

Within `cargo` you can define properties for remote containers in a closure named `remote`:

* `protocol`: The protocol of the remote container (defaults to `http`).
* `hostname`: The hostname of the remote container.
* `username`: The username credential for the remote container (optional).
* `password`: The password credential for the remote container (optional).

Within `cargo` you can define properties for local containers in a closure named `local`:

* `jvmArgs`: The JVM arguments for a local container.
* `outputFile`: The log file of your local container (defaults to writing to the console).
* `logFile`: The Cargo log file of your local container (defaults to writing to the console).
* `logLevel`: The log level to run the container with (optional). The valid levels are `low`, `medium` and `high`.
* `homeDir`: The home directory of your local container installation.
* `configHomeDir`: The home directory of your local container's configuration.
* `configFile`: The [configuration files](http://cargo.codehaus.org/Configuration+files+option) you want to add to your
container's configuration. The `configFile` is a closure itself and requires you to provide the attributes `file` and `todir`.
Multiple configuration file be defined by creating more than one `configFile` closure.
* `rmiPort`: The port to use when communicating with this server, for example to start and stop it.
* `timeout`: The timeout (in ms) in which to determine if the container is successfully started or stopped (defaults to 120000ms).
* `extraClasspath`: A [`FileCollection`](http://www.gradle.org/docs/current/javadoc/org/gradle/api/file/FileCollection.html)
that provides extra elements to the local [container classpath](http://cargo.codehaus.org/Container+Classpath) (optional).
* `sharedClasspath`: A [`FileCollection`](http://www.gradle.org/docs/current/javadoc/org/gradle/api/file/FileCollection.html)
that provides extra elements to the [application classpath](http://cargo.codehaus.org/Application+Classpath), and not to the
local container (optional).

### Container properties

Within `local` and `remote` you can define container-specific properties. These properties can be looked up on
the Cargo homepage. The following example shows how to set the AJP port for a local Tomcat container:

    cargo {
        local {
            containerProperties {
                property 'cargo.tomcat.ajp.port', 9099
            }
        }
    }

### System properties

Local containers can use system properties passed to it. The following example shows how to set a single system property named `myproperty`:

    cargo {
        local {
            systemProperties {
                property 'myproperty', 'myvalue'
            }
        }
    }

### Automatically bootstrapping a local container

If you decide to use the [ZIP installer](http://cargo.codehaus.org/Installer) Cargo will automatically download your container. You can
define its properties in the closure `installer`. The installer only applies to "local" Cargo tasks.

* `installUrl`: The URL to download the container distribtion from.
* `downloadDir`: Target directory to download the container distribution to.
* `extractDir`: Directory to extract the downloaded container distribution to.

Please refer to the individual configuration properties on the Cargo homepage. All of these properties can be overridden
by project properties. The name of the project properties is the same as in the Cargo manual.

### Example

    cargo {
        containerId = 'tomcat6x'
        port = 9090

        deployable {
            context = 'myawesomewebapp'
        }

        remote {
            hostname = 'cloud.internal.it'
            username = 'superuser'
            password = 'secretpwd'
        }

        local {
            homeDir = file('/home/user/dev/tools/apache-tomcat-6.0.32')
            outputFile = file('build/output.log')
            timeout = 60000

            containerProperties {
                property 'cargo.tomcat.ajp.port', 9099
            }
        }
    }

## Project properties

The convention properties can be overridden by project properties via `gradle.properties` or `-P` command line parameter:

* `cargo.container.id`: Overrides the convention property `containerId`.
* `cargo.port`: Overrides the convention property `port`.
* `cargo.timeout`: Overrides the convention property `timeout`.
* `cargo.protocol`: Overrides the convention property `protocol`.
* `cargo.hostname`: Overrides the convention property `hostname`.
* `cargo.username`: Overrides the convention property `username`.
* `cargo.password`: Overrides the convention property `password`.
* `cargo.jvmargs`: Overrides the convention property `jvmArgs`.
* `cargo.output`: Overrides the convention property `output`.
* `cargo.log`: Overrides the convention property `log`.
* `cargo.log.level`: Overrides the convention property `logLevel`.
* `cargo.home.dir`: Overrides the convention property `homeDir`.

## FAQ

**I want to automatically assemble my project's artifact when executing a Cargo deployment task.**

The task `cargoRunLocal` does not automatically depend on the `assemble` task. The reason behind that is that you might
not want to deploy your project's artifact or your project does not generate a WAR or EAR file. Instead you might want
to deploy one or more external artifacts. If your workflow looks like "compile the code", "generate the artifact" and "deploy"
then you make a Cargo deployment task depends on the `assemble` task. Here's one example:

    cargoRunLocal.dependsOn assemble

**I am working on a multi-project build. Can I apply the same Cargo configuration to all of my web projects?**

Gradle allows for filtering subprojects by certain criteria. To inject the relevant configuration from the root project
of your build, you will need to identify all subprojects that apply the War plugin (of course the same concept works
for Ear projects). Use the `configure` method to apply the Cargo plugin and your configuration as shown in the following
code snippet:

    def webProjects() {
        subprojects.findAll { subproject -> subproject.plugins.hasPlugin('war') }
    }

    gradle.projectsEvaluated {
        configure(webProjects()) {
            apply plugin: 'com.bmuschko.cargo'

            cargo {
                containerId = 'tomcat7x'

                remote {
                    hostname = 'localhost'
                    username = 'manager'
                    password = 'manager'
                }
            }
        }
    }

**I would like to deploy multiple artifacts to my container. How do I do that?**

You would specify each artifact in a separate `deployable` closure. Each of the closures should assign a unique URL context.
The following example demonstrates how a Cargo setup with three different artifacts deployed to a local Tomcat:

    cargo {
        containerId = 'tomcat6x'
        port = 9090

        deployable {
            file = file('/home/foo/bar/web-services.war')
            context = 'web-services'
        }

        deployable {
            file = file('/home/foo/bar/web-app.war')
            context = 'web-app'
        }

        deployable {
            file = file('/home/foo/bar/enterprise-app.ear')
            context = 'enterprise-app'
        }

        local {
            homeDir = file('/home/user/dev/tools/apache-tomcat-6.0.32')
        }
    }

**Is there a way to let Cargo automatically install the container I'd like to use?**

Cargo allows for defining a container that gets automatically downloaded and installed on your local disk. All you need to
do is to specify the `installer` closure. The following code snippet downloads, installs and uses Tomcat 7:

    cargo {
        containerId = 'tomcat7x'

        local {
            installer {
                installUrl = 'http://apache.osuosl.org/tomcat/tomcat-7/v7.0.27/bin/apache-tomcat-7.0.27.zip'
                downloadDir = file("$buildDir/download")
                extractDir = file("$buildDir/extract")
            }
        }
    }

**I'd like to add a configuration file to my local container. How do I do that?**

For local containers a closure named `configFile` can be used that defines the source file and directory you would like
to use the file from at runtime. If you need more than one just create multiple `configFile` closures.

    cargo {
        containerId = 'jboss5x'

        local {
            configFile {
                file = file('src/main/jboss5/login-config.xml')
                toDir = 'conf'
            }

            configFile {
                file = file('src/main/jboss5/sample-roles.properties')
                toDir = 'conf/props'
            }

            configFile {
                file = file('src/main/jboss5/sample-users.properties')
                toDir = 'conf/props'
            }
        }
    }

To add binary file(s) you should use `file` closure(s) instead:

    cargo {
        containerId = 'glassfish3x'

        local {
            file {
                file = file('../config/db/mysql-connector-java-5.1.23-bin.jar')
                toDir = 'lib'
            }
        }
    }

**I want to set up and configure my own Cargo task for more than one container. Can this be done?**

Absolutely. The Cargo base plugin provides all tasks needed to set up deployment tasks. All you need to do is to create one
or more tasks and configure the mandatory properties. The following example shows how to set up local container tasks
for Tomcat and Jetty:

    apply plugin: 'com.bmuschko.cargo-base'

    task myTomcatRun(type: com.bmuschko.gradle.cargo.tasks.local.CargoRunLocal) {
        containerId = 'tomcat7x'
        homeDir = file('/home/user/dev/tools/apache-tomcat-7.0.42')
    }

    task myJettyRun(type: com.bmuschko.gradle.cargo.tasks.local.CargoRunLocal) {
        containerId = 'jetty9x'
        homeDir = file('/home/user/dev/tools/jetty-distribution-9.0.4.v20130625')
    }

**I'd like to create deployment tasks for a rolling deployment to multiple remote containers. How do I do this?**

Gradle allows for dynamically creating tasks based on your build script logic. The following example shows how to create
three Tomcat deployment tasks and how to configure them with the help of a simple data structure. At the end of the script we
also add another task that triggers the deployment to all remote containers.

    class RemoteContainer {
        String name
        String hostname
        Integer port
        String username
        String password
    }

    def remoteContainers = [new RemoteContainer(name: 'tomcat1', hostname: 'remote-tomcat1',
                                                port: 9090, username: 'admin', password: 's3cr3t'),
                            new RemoteContainer(name: 'tomcat2', hostname: 'remote-tomcat2',
                                                port: 8050, username: 'deployer', password: 'qwerty'),
                            new RemoteContainer(name: 'tomcat3', hostname: 'remote-tomcat3',
                                                port: 8888, username: 'su', password: 'powerful')]

    apply plugin: 'com.bmuschko.cargo-base'

    remoteContainers.each { config ->
        task "deployRemote${config.name.capitalize()}"(type: com.bmuschko.gradle.cargo.tasks.remote.CargoDeployRemote) {
            description = "Deploys WAR to remote Tomcat '${config.name}'."
            containerId = 'tomcat7x'
            hostname = config.hostname
            port = config.port
            username = config.username
            password = config.password
        }
    }

    task deployToAllRemoteTomcats {
        dependsOn remoteContainers.collect { "deployRemote${it.name.capitalize()}" }
        description = 'Deploys to all remote Tomcat containers.'
        group = 'deployment'
    }

**Before a remote deployment I would like to restart my container. Can this be done?**

Yes, this is possible with the help of the [Cargo daemon](http://cargo.codehaus.org/Cargo+Daemon) functionality. Please
refer to the Cargo online documentation for setting up the Cargo daemon JVM process and configuring a container. With
this plugin, you can use custom tasks to start and stop a container. The following example stops, starts and then redeploys
an artifact.

    apply plugin: 'com.bmuschko.cargo'

    cargo {
        ...
    }

    ext.tomcat7HandleId = 'tomcat7'

    task cargoDaemonStop(type: com.bmuschko.gradle.cargo.tasks.daemon.CargoDaemonStop) {
        handleId = tomcat7HandleId
    }

    task cargoDaemonStart(type: com.bmuschko.gradle.cargo.tasks.daemon.CargoDaemonStart) {
        handleId = tomcat7HandleId
    }

    cargoDaemonStart.mustRunAfter cargoDaemonStop
    cargoRedeployRemote.dependsOn cargoDaemonStop, cargoDaemonStart

