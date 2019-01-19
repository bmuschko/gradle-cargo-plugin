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

/**
 * Defines Cargo extension.
 */
class CargoPluginExtension {

    private final Project project

    String containerId
    Integer port = 8080
    Integer timeout
    def deployables = []
    CargoRemoteTaskConvention remote = new CargoRemoteTaskConvention()
    CargoLocalTaskConvention local

    CargoPluginExtension(Project project) {
        this.project = project
        this.local = new CargoLocalTaskConvention(project)
    }

    def cargo(Closure closure) {
        closure.delegate = this
        closure()
    }

    def deployable(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        def deployableClosureDelegate = new DeployableClosureDelegate(project)
        closure.delegate = deployableClosureDelegate
        deployables << deployableClosureDelegate.deployable
        closure()
    }

    def remote(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = remote
        closure()
    }

    def local(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = local
        closure()
    }

    private static class DeployableClosureDelegate {

        @Delegate
        final Deployable deployable = new Deployable()
        private final Project project

        DeployableClosureDelegate(Project project) {
            this.project = project
        }

        void setFile(Object file) {
            deployable.files = project.files(file)
        }
    }

}
