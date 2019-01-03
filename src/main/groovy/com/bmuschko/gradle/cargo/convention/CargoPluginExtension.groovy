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
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal

import java.time.Duration

/**
 * Defines Cargo extension.
 */
class CargoPluginExtension {
    final Property<String> containerId
    final Property<Integer> port
    final Property<Duration> timeout
    final ListProperty<Deployable> deployables
    private Project project

    CargoPluginExtension(Project project) {
        this.project = project
        containerId = project.objects.property(String)
        port = project.objects.property(Integer)
        port.convention(8080)
        timeout = project.objects.property(Duration)
        deployables = project.objects.listProperty(Deployable)
    }

    def cargo(Closure closure) {
        closure.delegate = this
        closure()
    }


    def deployable(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        def deployableClosureDelegate = new DeployableClosureDelegate(project)
        closure.delegate = deployableClosureDelegate
        deployables.add(deployableClosureDelegate.deployable)
        closure()
    }

    private static class DeployableClosureDelegate {

        @Delegate
        final Deployable deployable

        @Internal
        private final Project project

        DeployableClosureDelegate(Project project) {
            this.project = project
            this.deployable = new Deployable(project)
        }

        void setFile(Object file) {
            deployable.setFile(project.file(file))
        }


        void setFiles(Object file) {
            deployable.setConfiguration(project.files(file))
        }

        void setContext(String context) {
            deployable.setContext(context)
        }
    }

}
