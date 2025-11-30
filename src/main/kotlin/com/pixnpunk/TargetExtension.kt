/**
 * Copyright 2019 Marco Freudenberger Copyright 2023 Welby Seely
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

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

open class TargetExtension @Inject constructor(project: Project, val name: String?) {

    // parameters used by config and build step
    val executable: Property<String> = project.objects.property()
    val workingFolder: DirectoryProperty = project.objects.directoryProperty()

    // parameters used by config step
    val sourceFolder: DirectoryProperty = project.objects.directoryProperty()
    val configurationTypes: Property<String> = project.objects.property()
    val installPrefix: Property<String> = project.objects.property()
    val generator: Property<String> = project.objects.property() // for example: "Visual Studio 16 2019"
    val platform: Property<String> = project.objects.property() // for example "x64" or "Win32" or "ARM" or "ARM64"
    val toolset: Property<String> = project.objects.property() // for example "v142", supported on vs > 10.0
    val buildSharedLibs: Property<Boolean> = project.objects.property()
    val buildStaticLibs: Property<Boolean> = project.objects.property()
    val defs: MapProperty<String, String> = project.objects.mapProperty()

    // parameters used on build step
    val buildConfig: Property<String> = project.objects.property()
    val buildTarget: Property<String> = project.objects.property()
    val buildClean: Property<Boolean> = project.objects.property()
}