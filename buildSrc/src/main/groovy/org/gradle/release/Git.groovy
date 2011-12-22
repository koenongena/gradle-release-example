package org.gradle.build
/*
 * Copyright 2010 the original author or authors.
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


import org.gradle.api.Project

class Git {
    private final Project project

    def Git(Project project) {
        this.project = project
    }

    def osExecutables() {
        def os = System.getProperty("os.name").toLowerCase()
        if (os.contains("windows")) {
            return [executable: 'cmd', args:['/c']]
        }

        return [executable: 'git', args:[]]

    }

    def checkNoModifications() {


        println 'checking for modifications'
        def stdout = new ByteArrayOutputStream()
        project.exec {
            executable = osExecutables().executable
            args = osExecutables().args + ['git', 'status', '--porcelain']
            standardOutput = stdout
        }
        if (stdout.toByteArray().length > 0) {
            throw new RuntimeException('Uncommited changes found in the source tree:\n' + stdout.toString())
        }
    }

    def tag(String tag, String message) {
        println "tagging with $tag"
        project.exec {
            executable = osExecutables().executable
            args = osExecutables().args + ['git', 'tag', '-a', tag, '-m', message]
        }
    }

    def branch(String branch) {
        println "creating branch $branch"
        project.exec {
            executable = osExecutables().executable
            args = osExecutables().args + ['git', 'branch', branch]
        }
    }
}