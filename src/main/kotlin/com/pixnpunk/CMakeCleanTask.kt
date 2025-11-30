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
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.*
import java.io.File

abstract class CMakeCleanTask : DefaultTask() {

    @get:OutputDirectory
    abstract val workingFolder: DirectoryProperty

    init {
        group = "cmake"
        description = "Clean CMake configuration"
    }

    private fun deleteDirectory(directoryToBeDeleted: File): Boolean {
        directoryToBeDeleted.listFiles()?.forEach { file ->
            deleteDirectory(file)
        }
        return directoryToBeDeleted.delete()
    }

    @TaskAction
    fun clean() {
        val workingFolder = workingFolder.asFile.get().getAbsoluteFile()
        if (workingFolder.exists()) {
            logger.info("Deleting folder $workingFolder")
            if (!deleteDirectory(workingFolder)) {
                throw GradleException("Could not delete working folder $workingFolder")
            }
        }
    }
}