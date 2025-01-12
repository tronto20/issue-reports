pluginManagement {
    fun property(name: String) = extra[name] as String

    val kotlinVersion = property("kotlin.version")
    val springBootVersion = property("spring.boot.version")
    val graalvmBuildToolsVersion = property("graalvm.native.tools.version")
    val restdocsApiSpecVersion = property("restdocs.api-spec.version")

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("org.springframework.boot") version springBootVersion
        id("org.graalvm.buildtools.native") version graalvmBuildToolsVersion
        id("com.epages.restdocs-api-spec") version restdocsApiSpecVersion
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "issue-reports"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":dependencies")
include(
    ":ep-1",
    ":ep-2",
    ":ep-4",
    ":ep-12",
    ":ep-12:before",
    ":ep-12:after",
)
