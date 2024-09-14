plugins {
    `java-platform`
}
fun property(name: String) = properties[name] as String

javaPlatform {
    this.allowDependencies()
}

dependencies {
    api(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:${property("kotlin.coroutines.version")}"))
    api(platform("org.jetbrains.kotlinx:kotlinx-serialization-bom:${property("kotlin.serialization.version")}"))
    api(platform("org.springframework.boot:spring-boot-dependencies:${property("spring.boot.version")}"))
    api(platform("io.kotest:kotest-bom:5.8.0"))
    api(platform("org.testcontainers:testcontainers-bom:1.19.4"))
    constraints {
        api("io.github.oshai:kotlin-logging-jvm:5.1.0")
        api("io.micrometer:context-propagation:1.1.0")
        api("io.kotest.extensions:kotest-extensions-spring:1.1.3")
        api("io.mockk:mockk:1.13.8")
        api("com.ninja-squad:springmockk:4.0.2")
    }
}
