package dev.tronto.issuereports.buildsrc

import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension


@Suppress("UNCHECKED_CAST")
fun <T : Any> ExtraPropertiesExtension.getOrPut(key: String, value: () -> T): T {
    return if (has(key)) {
        get(key) as T
    } else {
        value().also { set(key, it) }
    }
}

fun Project.property(name: String, default: String): String {
    return properties[name]?.toString() ?: default
}

fun Project.property(name: String, default: Int): Int {
    return properties[name]?.toString()?.toIntOrNull() ?: default
}
