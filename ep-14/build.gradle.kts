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
    implementation(platform("org.springframework.shell:spring-shell-dependencies:3.3.3"))
    implementation(projects.ep14.starter)
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("org.springframework.shell:spring-shell-starter")
    runtimeOnly("org.springframework.shell:spring-shell-starter-jansi")
    runtimeOnly("org.springframework.shell:spring-shell-starter-jni")
    runtimeOnly("org.springframework.shell:spring-shell-starter-jna")
}
