/**
 * Copyright 2019 Marco Freudenberger Copyright 2023 Welby Seely Copyright 2025 l3ger0j
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.pixnpunk

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

class CMakePlugin : Plugin<Project> {

    companion object {
        private const val CMAKE_GENERATORS = "cmakeGenerators"
        private const val CMAKE_CONFIGURE = "cmakeConfigure"
        private const val CMAKE_BUILD = "cmakeBuild"
        private const val CMAKE_CLEAN = "cmakeClean"
    }

    override fun apply(project: Project) {
        project.plugins.apply("base")

        val extension = project.extensions.create<CMakePluginExtension>("cmake", project)

        project.afterEvaluate {
            val tasks = project.tasks
            if (extension.targets.targetContainer.isEmpty()) {
                tasks.register<CMakeConfigureTask>(CMAKE_CONFIGURE) {
                    executable.set(extension.executable)
                    workingFolder.set(extension.workingFolder)
                    sourceFolder.set(extension.sourceFolder)
                    configurationTypes.set(extension.configurationTypes)
                    installPrefix.set(extension.installPrefix)
                    generator.set(extension.generator)
                    platform.set(extension.platform)
                    toolset.set(extension.toolset)
                    buildSharedLibs.set(extension.buildSharedLibs)
                    buildStaticLibs.set(extension.buildStaticLibs)
                    def.set(if (extension.defs.isPresent) extension.defs else extension.def)
                }

                tasks.register<CMakeBuildTask>(CMAKE_BUILD) {
                    executable.set(extension.executable)
                    workingFolder.set(extension.workingFolder)
                    buildConfig.set(extension.buildConfig)
                    buildTarget.set(extension.buildTarget)
                    buildClean.set(extension.buildClean)
                }

                tasks.register<CMakeCleanTask>(CMAKE_CLEAN) {
                    workingFolder.set(extension.workingFolder)
                }

                tasks.register<CMakeGeneratorsTask>(CMAKE_GENERATORS) {
                    executable.set(extension.executable)
                }
            } else {
                extension.targets.targetContainer.asMap.forEach { (_, target) ->
                    tasks.register<CMakeConfigureTask>(CMAKE_CONFIGURE) {
                        configureFromProject()
                        if (target.executable.isPresent) executable.set(target.executable)
                        if (target.workingFolder.isPresent) workingFolder.set(target.workingFolder)
                        if (target.sourceFolder.isPresent) sourceFolder.set(target.sourceFolder)
                        if (target.configurationTypes.isPresent) configurationTypes.set(target.configurationTypes)
                        if (target.installPrefix.isPresent) installPrefix.set(target.installPrefix)
                        if (target.generator.isPresent) generator.set(target.generator)
                        if (target.platform.isPresent) platform.set(target.platform)
                        if (target.toolset.isPresent) toolset.set(target.toolset)
                        if (target.buildSharedLibs.isPresent) buildSharedLibs.set(target.buildSharedLibs)
                        if (target.buildStaticLibs.isPresent) buildStaticLibs.set(target.buildStaticLibs)
                        if (target.defs.isPresent) def.set(target.defs)
                    }

                    tasks.register<CMakeBuildTask>(CMAKE_BUILD) {
                        configureFromProject()
                        if (target.executable.isPresent) executable.set(target.executable)
                        if (target.workingFolder.isPresent) workingFolder.set(target.workingFolder)
                        if (target.buildConfig.isPresent) buildConfig.set(target.buildConfig)
                        if (target.buildTarget.isPresent) buildTarget.set(target.buildTarget)
                        if (target.buildClean.isPresent) buildClean.set(target.buildClean)
                    }

                    tasks.register<CMakeCleanTask>(CMAKE_CLEAN) {
                        if (target.workingFolder.isPresent) workingFolder.set(target.workingFolder)
                    }

                    tasks.register<CMakeGeneratorsTask>(CMAKE_GENERATORS) {
                        if (target.executable.isPresent) executable.set(target.executable)
                    }
                }
            }

            tasks.withType<CMakeBuildTask>().configureEach { dependsOn(tasks.withType<CMakeConfigureTask>()) }

            tasks.named("clean") { dependsOn("cmakeClean") }

            tasks.named("build") { dependsOn(tasks.withType<CMakeBuildTask>()) }
        }
    }
}