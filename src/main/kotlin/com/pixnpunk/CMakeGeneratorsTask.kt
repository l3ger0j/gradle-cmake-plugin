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
import org.gradle.api.GradleScriptException
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.IOException

abstract class CMakeGeneratorsTask : DefaultTask() {

    @get:Input
    @get:Optional
    abstract val executable: Property<String>

    init {
        group = "cmake"
        description = "List available CMake generators"
    }

    @TaskAction
    fun generators() {
        val pb = ProcessBuilder(executable.getOrElse("cmake"), "--help")
        try {
            val process = pb.start()
            var foundGenerators = false
            process.inputStream.bufferedReader().forEachLine { line ->
                if (line == "Generators") {
                    foundGenerators = true
                }
                if (foundGenerators) {
                    logger.log(LogLevel.QUIET, line)
                }
            }
            process.waitFor()
        } catch (e: IOException) {
            throw GradleScriptException("cmake --help failed.", e)
        } catch (e: InterruptedException) {
            throw GradleScriptException("cmake --help failed.", e)
        }
    }
}