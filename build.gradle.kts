plugins {
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
    `kotlin-dsl`
}

group = "com.pixnpunk"
version = "1.0.0-alpha"

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenLocal()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("cmakePlugin") {
            id = "com.pixnpunk.gradle-cmake-plugin"
            displayName = "Gradle CMake plugin"
            description = "Plugin to seamlessly integrate Cmake with Gradle"
            implementationClass = "com.pixnpunk.CMakePlugin"
        }
    }
}

publishing {
    repositories {
        maven {
        }
    }
}
