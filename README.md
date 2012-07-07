# Gradle Cargo plugin

![Cargo Logo](http://docs.codehaus.org/download/attachments/27913/cargo-banner-left.png)

The plugin provides deployment capabilities for web applications to local and remote containers in any given
Gradle build by leveraging the [Cargo Ant tasks](http://cargo.codehaus.org/Ant+support). The plugin supports WAR and EAR
artifacts.

## Usage

To use the Cargo plugin, include in your build script:

    apply plugin: 'cargo'

The plugin JAR needs to be defined in the classpath of your build script. You can either get the plugin from the GitHub download
section or upload it to your local repository. To define the Cargo dependencies please use the `cargo` configuration name
in your `dependencies` closure. Remote deployment functionality will only work with a Cargo version >= 1.1.0 due to a bug
in the library. Please see [CARGO-962](https://jira.codehaus.org/browse/CARGO-962) for more information.

    buildscript {
        repositories {
            add(new org.apache.ivy.plugins.resolver.URLResolver()) {
                name = 'GitHub'
                addArtifactPattern 'http://cloud.github.com/downloads/[organisation]/[module]/[module]-[revision].[ext]'
            }
        }

        dependencies {
            classpath 'bmuschko:gradle-cargo-plugin:0.5.3'
        }
    }

    dependencies {
        def cargoVersion = '1.2.2'
        cargo "org.codehaus.cargo:cargo-core-uberjar:$cargoVersion",
              "org.codehaus.cargo:cargo-ant:$cargoVersion"
    }

## Tasks

The Cargo plugin defines the following tasks:

* `cargoDeployRemote`: Deploys web application to remote container.
* `cargoUndeployRemote`: Undeploys a web application from remote container.
* `cargoRedeployRemote`: Redeploys web application to remote container.
* `cargoRunLocal`: Starts the local container, deploys web application to it and wait for the user to press `CTRL + C` to stop.
* `cargoStartLocal`: Starts the local container, deploys web application to it and then do other tasks (for example, execute tests).
* `cargoStopLocal`: Stops local container.

## Project layout

The Cargo plugin uses the same layout as the War plugin.

## Convention properties

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
* `output`: The log file of your local container (defaults to writing to the console).
* `log`: The Cargo log file of your local container (defaults to writing to the console).
* `logLevel`: The log level to run the container with (optional). The valid levels are `low`, `medium` and `high`.
* `homeDir`: The home directory of your local container.

Within `local` you can define properties for specific local containers. At the moment the following containers are supported
defined by these closures:

* `jetty`: Jetty
    * `createContextXml`
    * `sessionPath`
    * `useFileMappedBuffer`
* `jonas`: Jonas
    * `jmsPort`
    * `serverName`
    * `servicesList`
    * `domainName`
* `jrun`: JRun
    * `home`
* `tomcat`: Tomcat
    * `webappsDir`
    * `copyWars`
    * `contextReloadable`
    * `ajpPort`
* `weblogic`: WebLogic
    * `adminUser`
    * `adminPassword`
    * `beaHome`
    * `server`

If you decide to use the [ZIP installer](http://cargo.codehaus.org/Installer) Cargo will automatically download your container. You can
define its properties in the closure `installer`. The installer only applies to "local" Cargo tasks.

* `installUrl`: The URL to download the container distribtion from.
* `downloadDir`: Target directory to download the container distribution to.
* `extractDir`: Directory to extract the downloaded container distribution to.

Please refer to the individual configuration properties on the Cargo homepage. All of these properties can be overriden
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
            output = file('build/output.log')

            tomcat {
                ajpPort = 9091
            }
        }
    }

## Project properties

The convention properties can be overridden by project properties via `gradle.properties` or `-P` command line parameter:

* `cargo.container.id`: Overrides the convention property `containerId`.
* `cargo.port`: Overrides the convention property `port`.
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