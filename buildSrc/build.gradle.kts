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
    val kotlinterVersion = property("kotlinter.version")
    val springBootVersion = property("spring.boot.version")
    val graalvmBuildToolsVersion = property("graalvm.native.tools.version")

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jmailen.gradle:kotlinter-gradle:$kotlinterVersion")
    // 둘은 동시에 적용해야 함.
//    implementation("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
//    implementation("org.graalvm.buildtools.native:org.graalvm.buildtools.native.gradle.plugin:$graalvmBuildToolsVersion")
}

gradlePlugin {
    plugins {
        create("issue-reports") {
            id = "issuereports"
            implementationClass = "dev.tronto.issuereports.buildsrc.IssueReportsPlugin"
        }
    }
}


