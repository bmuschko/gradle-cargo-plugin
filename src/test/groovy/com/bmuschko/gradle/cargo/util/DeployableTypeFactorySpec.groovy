package com.bmuschko.gradle.cargo.util

import com.bmuschko.gradle.cargo.DeployableType
import com.bmuschko.gradle.cargo.DeployableTypeFactory
import com.bmuschko.gradle.cargo.convention.Deployable
import spock.lang.Specification
import spock.lang.Unroll

class DeployableTypeFactorySpec extends Specification {
    FileUtil fileUtil = Mock()

    def setup() {
        DeployableTypeFactory.instance.fileUtil = fileUtil
    }

    @Unroll
    def "determines type for '#description'"() {
        given:
        File givenFile = new File(givenFilename)

        when:
        DeployableType deployableType = DeployableTypeFactory.instance.getType(new Deployable(file: givenFile))

        then:
        1 * fileUtil.getExtension(givenFile) >> fileExtension
        deployableType.type == expectedDeployableType.type

        where:
        givenFilename             | fileExtension | expectedDeployableType  | description
        '/User/ben/app/myapp.war' | 'war'         | DeployableType.WAR      | 'WAR file'
        '/User/ben/app/myapp.ear' | 'ear'         | DeployableType.EAR      | 'EAR file'
        '/User/ben/app/myapp'     | ''            | DeployableType.EXPLODED | 'exploded WAR'
    }
}
