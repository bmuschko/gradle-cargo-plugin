package org.gradle.api.plugins.cargo

import org.gradle.api.plugins.cargo.property.LocalJbossTaskProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * Deploys WAR to local JBoss container.
 *
 * @author voitau
 */
class LocalJbossTask extends LocalContainerTask {
    @Input @Optional Integer invokerPoolPort
    @Input @Optional Integer jrmpInvokerPort
    @Input @Optional Integer namingPort
    @Input @Optional String configuration

    @Override
    void setContainerSpecificProperties() {
        if(getInvokerPoolPort()) {
            ant.property(name: LocalJbossTaskProperty.INVOKER_POOL_PORT.name, value: getInvokerPoolPort())
        }
        if(getJrmpInvokerPort()) {
            ant.property(name: LocalJbossTaskProperty.JRMP_INVOKER_PORT.name, value: getJrmpInvokerPort())
        }
        if(getNamingPort()) {
            ant.property(name: LocalJbossTaskProperty.NAMING_PORT.name, value: getNamingPort())
        }
        if(getConfiguration()) {
            ant.property(name: LocalJbossTaskProperty.CONFIGURATION.name, value: getConfiguration())
        }
    }

}
