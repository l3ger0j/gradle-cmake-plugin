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