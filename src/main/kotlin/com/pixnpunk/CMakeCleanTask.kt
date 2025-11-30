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