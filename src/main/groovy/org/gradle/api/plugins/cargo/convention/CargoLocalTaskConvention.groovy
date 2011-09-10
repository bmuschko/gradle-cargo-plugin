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

/**
 * Defines Cargo local task convention.
 *
 * @author Benjamin Muschko
 */
class CargoLocalTaskConvention {
    String logLevel
    File homeDir
    CargoLocalJettyConvention jetty = new CargoLocalJettyConvention()
    CargoLocalJonasConvention jonas = new CargoLocalJonasConvention()
    CargoLocalJRunConvention jrun = new CargoLocalJRunConvention()
    CargoLocalTomcatConvention tomcat = new CargoLocalTomcatConvention()
    CargoLocalWeblogicConvention weblogic = new CargoLocalWeblogicConvention()

    def jetty(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = jetty
        closure()
    }

    def jonas(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = jonas
        closure()
    }

    def jrun(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = jrun
        closure()
    }

    def tomcat(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = tomcat
        closure()
    }

    def weblogic(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = weblogic
        closure()
    }
}
