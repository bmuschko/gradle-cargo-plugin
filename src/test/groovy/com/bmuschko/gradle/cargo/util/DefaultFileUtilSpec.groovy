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

import org.gradle.util.GFileUtils
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Filename utilities unit tests.
 *
 * @author Benjamin Muschko
 */
class DefaultFileUtilSpec extends Specification {
    FileUtil fileUtil = new DefaultFileUtil()
    File tempDir

    def setup() {
        tempDir = createDirectory('build/tests/tmp')
    }

    def cleanup() {
        GFileUtils.deleteDirectory(tempDir)
    }

    def "throws exception for non-existent file"() {
        when:
        fileUtil.getExtension(new File('unknownfile'))

        then:
        thrown(IllegalArgumentException)
    }

    @Unroll
    def "get file extension for file with name '#filename'"() {
        when:
        String extension = fileUtil.getExtension(createFile(tempDir, filename))

        then:
        extension == expectedExtension

        where:
        filename              | expectedExtension
        '/Users/ben/test.war' | 'war'
        '/Users/ben/test'     | ''
    }

    @Unroll
    def "get file extension for directory with name '#dirname'"() {
        when:
        String extension = fileUtil.getExtension(createDirectory(tempDir, dirname))

        then:
        extension == expectedExtension

        where:
        dirname               | expectedExtension
        '/Users/ben/test'     | ''
        '/Users/ben/test-1.0' | ''
    }

    private File createDirectory(String dirname) {
        File dir = new File(dirname)
        GFileUtils.mkdirs(dir)
        dir
    }

    private File createDirectory(File parent, String dirname) {
        File dir = new File(parent, dirname)
        GFileUtils.mkdirs(dir)
        dir
    }

    private File createFile(File parent, String filename) {
        File file = new File(parent, filename)
        GFileUtils.touch(file)
        file
    }
}
