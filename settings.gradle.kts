pluginManagement {
    fun property(name: String) = extra[name] as String

    val kotlinVersion = property("kotlin.version")
    val springBootVersion = property("spring.boot.version")

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("org.springframework.boot") version springBootVersion
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "issue-reports"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":dependencies")
include(":ep-1")