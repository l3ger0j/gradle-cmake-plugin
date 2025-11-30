package com.pixnpunk

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.getByType

/**
 * Configure Build Dir with CMake
 */
abstract class CMakeConfigureTask : DefaultTask() {

    @get:Input
    @get:Optional
    abstract val executable: Property<String>

    @get:OutputDirectory
    abstract val workingFolder: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceFolder: DirectoryProperty

    @get:Input
    @get:Optional
    abstract val configurationTypes: Property<String>

    @get:Input
    @get:Optional
    abstract val installPrefix: Property<String>

    @get:Input
    @get:Optional
    abstract val generator: Property<String> // for example: "Visual Studio 16 2019"

    @get:Input
    @get:Optional
    abstract val platform: Property<String> // for example "x64" or "Win32" or "ARM" or "ARM64", supported on vs > 8.0

    @get:Input
    @get:Optional
    abstract val toolset: Property<String> // for example "v142", supported on vs > 10.0

    @get:Input
    @get:Optional
    abstract val buildSharedLibs: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val buildStaticLibs: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val def: MapProperty<String, String>

    init {
        group = "cmake"
        description = "Configure a Build with CMake"
    }

    fun configureFromProject() {
        val ext = project.extensions.getByType<CMakePluginExtension>()
        executable.set(ext.executable)
        workingFolder.set(ext.workingFolder)
        sourceFolder.set(ext.sourceFolder)
        configurationTypes.set(ext.configurationTypes)
        installPrefix.set(ext.installPrefix)
        generator.set(ext.generator)
        platform.set(ext.platform)
        toolset.set(ext.toolset)
        buildSharedLibs.set(ext.buildSharedLibs)
        buildStaticLibs.set(ext.buildStaticLibs)
        def.set(ext.defs)
    }

    private fun buildCmdLine(): List<String> = buildList {
        add(executable.getOrElse("cmake"))

        generator.orNull?.takeIf { it.isNotBlank() }?.let {
            add("-G")
            add(it)
        }

        platform.orNull?.takeIf { it.isNotBlank() }?.let {
            add("-A")
            add(it)
        }

        toolset.orNull?.takeIf { it.isNotBlank() }?.let {
            add("-T")
            add(it)
        }

        configurationTypes.orNull?.takeIf { it.isNotBlank() }?.let {
            add("-DCMAKE_CONFIGURATION_TYPES=$it")
        }

        installPrefix.orNull?.takeIf { it.isNotBlank() }?.let {
            add("-DCMAKE_INSTALL_PREFIX=$it")
        }

        buildSharedLibs.orNull?.let {
            add("-DBUILD_SHARED_LIBS=${if (it) "ON" else "OFF"}")
        }

        buildStaticLibs.orNull?.let {
            add("-DBUILD_STATIC_LIBS=${if (it) "ON" else "OFF"}")
        }

        def.orNull?.forEach { (key, value) ->
            add("-D$key=$value")
        }

        add(sourceFolder.asFile.get().absolutePath)
    }

    @TaskAction
    fun configure() {
        val executor = CMakeExecutor(logger, name)
        executor.exec(buildCmdLine(), workingFolder.asFile.get())
    }
}
