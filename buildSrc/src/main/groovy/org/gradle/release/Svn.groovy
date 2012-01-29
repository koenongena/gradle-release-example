package org.gradle.release
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

class Svn {
    private final Project project

    def Svn(Project project) {
        this.project = project
    }

    def osExecutables() {
        def os = System.getProperty("os.name").toLowerCase()
        if (os.contains("windows")) {
            return [executable: 'cmd', args: ['/c', 'svn']]
        }

        return [executable: 'svn', args: []]

    }

    def hasUncommittedFiles() {
        println 'checking for modifications'
        def stdout = new ByteArrayOutputStream()
        project.exec {
            executable = osExecutables().executable
            args = osExecutables().args + ['status']
            standardOutput = stdout
        }

        return stdout.toByteArray().length > 0
    }

    def isLocalCopyUpToDate() {
        // TODO implement
        return true
    }

    def doesTagExist(String name) {
        assert project.svnRepoRoot
        String tags = listFiles("${project.svnRepoRoot}/tags")
        return tags.contains(name)
    }

    def doesBranchExist(String name) {
        assert project.svnRepoRoot
        String branches = listFiles("${project.svnRepoRoot}/branches")
        return branches.contains(name)
    }

    private String listFiles(String url) {
        def stdout = new ByteArrayOutputStream()
        project.exec {
            executable = osExecutables().executable
            args = osExecutables().args + ['ls', url]
            standardOutput = stdout
        }
        return stdout.toString()
    }

    def tag(String tag, String message) {
        println "tagging with $tag"
        assert project.svnRepoRoot
        copyTrunk('tags', tag, message)
    }

    def branch(String branch, String message) {
        println "creating branch $branch"
        assert project.svnRepoRoot
        copyTrunk('branches', branch, message)
    }

    private void copyTrunk(String rootFolder, String name, String message) {
        project.exec {
            executable = osExecutables().executable
            args = osExecutables().args + ['cp', "${project.svnRepoRoot}/trunk", "${project.svnRepoRoot}/$rootFolder/$name", '-m', message]
        }
    }

    def commitReleases(String message) {
        project.exec {
            executable = osExecutables().executable
            args = osExecutables().args + ['commit', '-m', message, "${project.relativePath(project.releases.releasesFile)}"]
        }
    }
}