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
    @Input @Optional String configuration

    @Override
    void setContainerSpecificProperties() {
        if(getInvokerPoolPort()) {
            ant.property(name: LocalJbossTaskProperty.INVOKER_POOL_PORT.name, value: getInvokerPoolPort())
        }
        if(getConfiguration()) {
            ant.property(name: LocalJbossTaskProperty.CONFIGURATION.name, value: getConfiguration())
        }
    }

}
