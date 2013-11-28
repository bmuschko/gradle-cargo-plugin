package org.gradle.api.plugins.cargo.convention

/**
 * Defines Cargo local JBoss convention.
 *
 * @author voitau
 */
class CargoLocalJbossConvention {
    Integer invokerPoolPort
    Integer jrmpInvokerPort
    Integer namingPort
    String configuration
}
