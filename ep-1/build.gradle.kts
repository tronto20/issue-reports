plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.serialization")
    id("org.springframework.boot")
    issuereports
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(projects.dependencies))
    implementation(kotlin("reflect"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest:kotest-extensions-junit5")
    testImplementation("io.kotest:kotest-assertions-core")
    testImplementation("io.kotest.extensions:kotest-extensions-spring")
    testImplementation("com.ninja-squad:springmockk")
    testImplementation("io.mockk:mockk")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
}
