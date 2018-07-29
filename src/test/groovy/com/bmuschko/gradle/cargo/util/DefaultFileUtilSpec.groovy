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
package com.bmuschko.gradle.cargo.util

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Filename utilities unit tests.
 */
class DefaultFileUtilSpec extends Specification {
    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    FileUtil fileUtil = new DefaultFileUtil()

    def "throws exception for non-existent file"() {
        when:
        fileUtil.getExtension(new File('unknownfile'))

        then:
        thrown(IllegalArgumentException)
    }

    @Unroll
    def "get file extension for file with name '#filename'"() {
        when:
        String extension = fileUtil.getExtension(temporaryFolder.newFile(filename))

        then:
        extension == expectedExtension

        where:
        filename   | expectedExtension
        'test.war' | 'war'
        'test'     | ''
    }

    @Unroll
    def "get file extension for directory with name '#dirname'"() {
        when:
        String extension = fileUtil.getExtension(temporaryFolder.newFolder(dirname))

        then:
        extension == expectedExtension

        where:
        dirname    | expectedExtension
        'test'     | ''
        'test-1.0' | ''
    }
}
