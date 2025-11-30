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

import org.gradle.api.GradleException
import org.gradle.api.GradleScriptException
import org.gradle.api.logging.Logger
import java.io.*
import java.util.concurrent.*

internal class CMakeExecutor(private val logger: Logger, private val taskName: String) {

    @Throws(GradleException::class)
    fun exec(cmdLine: List<String>, workingFolder: File) {
        // log command line parameters
        val commandString = cmdLine.joinToString(" ")
        logger.info("  CMakePlugin.task $taskName - exec: $commandString")

        // build process
        val processBuilder = ProcessBuilder(cmdLine).directory(workingFolder)
        val executor = Executors.newFixedThreadPool(2)
        try {
            // make sure working folder exists
            workingFolder.mkdirs()

            // start
            val process = processBuilder.start()

            val stdoutFuture = executor.submit { readStream(process.inputStream, true) }
            val stderrFuture = executor.submit { readStream(process.errorStream, false) }

            val exitCode = process.waitFor()

            warnIfTimeout(stdoutFuture, "CMakeExecutor[$taskName]Warn: timed out waiting for stdout to be closed.")
            warnIfTimeout(stderrFuture, "CMakeExecutor[$taskName]Warn: timed out waiting for stderr to be closed.")

            if (exitCode != 0) {
                throw GradleException("[$taskName]Error: CMAKE returned $exitCode")
            }
        } catch (e: IOException) {
            throw GradleScriptException("CMakeExecutor[$taskName].", e)
        } catch (e: InterruptedException) {
            throw GradleScriptException("CMakeExecutor[$taskName].", e)
        } catch (e: ExecutionException) {
            throw GradleScriptException("CMakeExecutor[$taskName].", e)
        } finally {
            executor.shutdown()
        }
    }

    private fun readStream(inputStream: InputStream, isStdOut: Boolean) {
        inputStream.bufferedReader().use { reader ->
            if (isStdOut) {
                reader.forEachLine { logger.info(it) }
            } else {
                reader.forEachLine { logger.error(it) }
            }
        }
    }

    private fun warnIfTimeout(future: Future<*>, message: String) {
        try {
            future.get(3, TimeUnit.SECONDS)
        } catch (_: TimeoutException) {
            logger.warn(message)
        }
    }
}

