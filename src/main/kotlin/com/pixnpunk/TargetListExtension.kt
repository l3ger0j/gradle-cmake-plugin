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

import groovy.lang.Closure
import groovy.lang.GroovyObjectSupport
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.container
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

open class TargetListExtension @Inject constructor(project: Project) : GroovyObjectSupport() {
    val targetContainer: NamedDomainObjectContainer<TargetExtension> =
        project.container { name ->
            project.objects.newInstance(name, project)
        }

    override fun invokeMethod(name: String, args: Any): Any {
        val arguments = args as? Array<*> ?: return super.invokeMethod(name, args)

        if (arguments.size == 1 && arguments[0] is Closure<*>) {
            val closure = arguments[0] as Closure<*>
            return targetContainer.create(name, closure)
        }

        return super.invokeMethod(name, args)
    }
}