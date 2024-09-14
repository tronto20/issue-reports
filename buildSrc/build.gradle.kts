import java.io.FileInputStream
import java.util.*

val properties = Properties().apply {
    FileInputStream(file("../gradle.properties")).use {
        load(it)
    }
}
fun property(name: String) = properties.getProperty(name)
plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    val kotlinVersion = property("kotlin.version")
    val springBootVersion = property("spring.boot.version")
    val kotlinterVersion = property("kotlinter.version")

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jmailen.gradle:kotlinter-gradle:$kotlinterVersion")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
}

gradlePlugin {
    plugins {
        create("issue-reports") {
            id = "issuereports"
            implementationClass = "dev.tronto.issuereports.buildsrc.IssueReportsPlugin"
        }
    }
}
