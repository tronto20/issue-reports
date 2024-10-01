package dev.tronto.issuereports.buildsrc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

internal class JvmInternalPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            val extension = target.extensions.getByType(KotlinJvmProjectExtension::class.java)
            val jdkVersion = target.property("jdk.version", 17)
            extension.jvmToolchain {
                languageVersion.set(JavaLanguageVersion.of(jdkVersion))
                vendor.set(JvmVendorSpec.BELLSOFT)
            }
            extension.compilerOptions {
                freeCompilerArgs.add("-Xjsr305=strict")
                jvmTarget.set(JvmTarget.fromTarget(jdkVersion.toString()))
            }
            target.tasks.withType(Test::class.java) {
                useJUnitPlatform()
            }
        }
    }
}
