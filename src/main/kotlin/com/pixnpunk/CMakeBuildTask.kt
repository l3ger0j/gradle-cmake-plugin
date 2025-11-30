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

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.getByType

/**
 * Build a configured Build with CMake
 */
abstract class CMakeBuildTask : DefaultTask() {

    @get:Input
    @get:Optional
    abstract val executable: Property<String>

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val workingFolder: DirectoryProperty

    @get:Input
    @get:Optional
    abstract val buildConfig: Property<String>

    @get:Input
    @get:Optional
    abstract val buildTarget: Property<String>

    @get:Input
    @get:Optional
    abstract val buildClean: Property<Boolean>

    init {
        group = "cmake"
        description = "Build a configured Build with CMake"
    }

    fun configureFromProject() {
        val ext = project.extensions.getByType<CMakePluginExtension>()
        executable.set(ext.executable)
        workingFolder.set(ext.workingFolder)
        buildConfig.set(ext.buildConfig)
        buildTarget.set(ext.buildTarget)
        buildClean.set(ext.buildClean)
    }

    private fun buildCmdLine(): List<String> = buildList {
        add(executable.getOrElse("cmake"))
        add("--build")
        add(".")

        buildConfig.orNull?.let {
            add("--config")
            add(it)
        }

        buildTarget.orNull?.let {
            add("--target")
            add(it)
        }

        if (buildClean.getOrElse(false)) {
            add("--clean-first")
        }
    }

    @TaskAction
    fun build() {
        val executor = CMakeExecutor(logger, name)
        executor.exec(buildCmdLine(), workingFolder.asFile.get())
    }
}
