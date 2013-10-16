package org.gradle.api.plugins.cargo.property

import groovy.util.logging.Slf4j

/**
 * Defines local JBoss task convention.
 *
 * @author voitau
 */
@Slf4j
enum LocalJbossTaskProperty implements TaskProperty {
    /** Effective for pre-JBoss 7. */
    INVOKER_POOL_PORT('cargo.jboss.invoker.pool.port', PropertyDataType.INTEGER),
    CONFIGURATION('cargo.jboss.configuration', PropertyDataType.STRING)

    static final Map PROPERTIES

    static {
        PROPERTIES = [:]

        values().each { property ->
            PROPERTIES.put(property.name, property)
        }
    }

    final String name
    final PropertyDataType type

    LocalJbossTaskProperty(String name, PropertyDataType type) {
        this.name = name
        this.type = type
    }

    static getPropertyForName(name) {
        def property = PROPERTIES[name]

        if(!property) {
            log.error "Unknown property: $name"
            throw new IllegalArgumentException("Unknown property: $name")
        }

        property
    }
}
