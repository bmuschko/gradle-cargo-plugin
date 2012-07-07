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
package org.gradle.api.plugins.cargo

import groovy.util.logging.Slf4j

/**
 * Defines supported containers.
 *
 * @author Benjamin Muschko
 */
@Slf4j
enum Container {
    GERONIMO_1X('geronimo1x', 'Geronimo 1.x'), GERONIMO_2X('geronimo2x', 'Geronimo 2.x'), GERONIMO_3X('geronimo3x', 'Geronimo 3.x'), GERONIMO_4X('geronimo4x', 'Geronimo 4.x'),
    GLASSFISH_2X('glassfish2x', 'Glassfish 2.x'), GLASSFISH_3X('glassfish3x', 'Glassfish 3.x'),
    JBOSS_3X('jboss3x', 'JBoss 3.x'), JBOSS_4X('jboss4x', 'JBoss 4.x'), JBOSS_4_2X('jboss42x', 'JBoss 4.2.x'), JBOSS_5X('jboss5x', 'JBoss 5.x'), JBOSS_5_1X('jboss51x', 'JBoss 5.1.x'), JBOSS_6X('jboss6x', 'JBoss 6.x'), JBOSS_6_1X('jboss61x', 'JBoss 6.1.x'), JBOSS_7X('jboss7x', 'JBoss 7.x'), JBOSS_7_1X('jboss71x', 'JBoss 7.1.x'),
    JETTY_4X('jetty4x', 'Jetty 4.x'), JETTY_5X('jetty5x', 'Jetty 5.x'), JETTY_6X('jetty6x', 'Jetty 6.x'), JETTY_7X('jetty7x', 'Jetty 7.x'), JETTY_8X('jetty8x', 'Jetty 8.x'),
    JO_1X('jo1x', 'jo! 1.x'),
    JONAS_4x('jonas4x', 'JOnAS 4.x'), JONAS_5x('jonas5x', 'JOnAS 5.x'),
    JRUN_4X('jrun4x', 'JRun 4.x'),
    OC4J_9X('oc4j9x', 'Oc4J 9.x'), OC4J_10X('oc4j10x', 'Oc4J 10.x'),
    RESIN_2X('resin2x', 'Resin 2.x'), RESIN_3X('resin3x', 'Resin 3.x'),
    TOMCAT_5X('tomcat5x', 'Tomcat 5.x'), TOMCAT_6X('tomcat6x', 'Tomcat 6.x'), TOMCAT_7X('tomcat7x', 'Tomcat 7.x'),
    WEBLOGIC_8X('weblogic8x', 'WebLogic 8.x'), WEBLOGIC_9X('weblogic9x', 'WebLogic 9.x'), WEBLOGIC_10X('weblogic10x', 'WebLogic 10.x'), WEBLOGIC_10_3X('weblogic103x', 'WebLogic 10.3.x')

    static final Map CONTAINERS

    static {
        CONTAINERS = [:] as TreeMap

        values().each { container ->
            CONTAINERS.put(container.id, container)
        }
    }

    static final EnumSet<Container> GERONIMO = EnumSet.range(GERONIMO_1X, GERONIMO_4X)
    static final EnumSet<Container> GLASSFISH = EnumSet.of(GLASSFISH_2X, GLASSFISH_3X)
    static final EnumSet<Container> JBOSS = EnumSet.range(JBOSS_3X, JBOSS_7_1X)
    static final EnumSet<Container> JETTY = EnumSet.range(JETTY_4X, JETTY_8X)
    static final EnumSet<Container> JO = EnumSet.of(JO_1X)
    static final EnumSet<Container> JONAS = EnumSet.of(JONAS_4x, JONAS_5x)
    static final EnumSet<Container> JRUN = EnumSet.of(JRUN_4X)
    static final EnumSet<Container> OC4J = EnumSet.of(OC4J_9X, OC4J_10X)
    static final EnumSet<Container> RESIN = EnumSet.of(RESIN_2X, RESIN_3X)
    static final EnumSet<Container> TOMCAT = EnumSet.range(TOMCAT_5X, TOMCAT_7X)
    static final EnumSet<Container> WEBLOGIC = EnumSet.range(WEBLOGIC_8X, WEBLOGIC_10_3X)
    final String id
    final String description

    private Container(String id, String description) {
        this.id = id
        this.description = description
    }

    static getContainerForId(id) {
        def container = CONTAINERS[id]

        if(!container) {
            log.error "Unknown container: ${id}"
            throw new IllegalArgumentException('Unknown container')
        }

        container
    }

    static getContainerIds() {
        CONTAINERS.keySet()
    }
}