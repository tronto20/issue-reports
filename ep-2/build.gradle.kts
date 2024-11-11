import dev.tronto.issuereports.buildsrc.tasks.PathExec

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.serialization")
    id("org.springframework.boot")
    id("org.graalvm.buildtools.native")
    issuereports
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(projects.dependencies))
    implementation(kotlin("reflect"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.kotest:kotest-assertions-core")

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest:kotest-extensions-junit5")
    testImplementation("io.kotest.extensions:kotest-extensions-spring")
    testImplementation("com.ninja-squad:springmockk")
    testImplementation("io.mockk:mockk")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
}

tasks.bootBuildImage {
    this.environment.set(mapOf("BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to "-Ob"))
}


tasks.register("runTestImage", PathExec::class.java) {
    dependsOn(tasks.bootBuildImage)
    this.group = "build"
    workingDir = project.projectDir
    executable = "docker"
    val imageName = tasks.bootBuildImage.get().imageName.get()
    setArgs(
        listOf(
            "run",
            "--rm",
            imageName
        )
    )
}

tasks.test {
    dependsOn("runTestImage")
}
