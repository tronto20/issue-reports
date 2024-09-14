package dev.tronto.issuereports.buildsrc

import org.gradle.api.Plugin
import org.gradle.api.Project

class IssueReportsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(JvmInternalPlugin::class.java)
        target.plugins.apply(FormatInternalPlugin::class.java)
    }
}

