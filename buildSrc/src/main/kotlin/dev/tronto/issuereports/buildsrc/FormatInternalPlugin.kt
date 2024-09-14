package dev.tronto.issuereports.buildsrc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jmailen.gradle.kotlinter.KotlinterPlugin
import org.jmailen.gradle.kotlinter.tasks.InstallPreCommitHookTask

internal class FormatInternalPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (!target.pluginManager.hasPlugin("org.jmailen.kotlinter")) {
            target.pluginManager.apply(KotlinterPlugin::class.java)
            if (target.rootProject == target) {
                target.tasks.register("installKotlinterPreCommitHook", InstallPreCommitHookTask::class.java) {
                    this.group = "build setup"
                    this.description = "Installs Kotlinter Git pre-commit hook"
                }

                target.tasks.getByName("prepareKotlinBuildScriptModel") {
                    dependsOn(target.tasks.getByName("installKotlinterPrePushHook"))
                    dependsOn(target.tasks.getByName("installKotlinterPreCommitHook"))
                }
            }
        }
    }
}
