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