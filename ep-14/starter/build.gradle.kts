plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    issuereports
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(projects.dependencies))
    implementation(kotlin("reflect"))
    implementation(platform("org.springframework.shell:spring-shell-dependencies:3.3.3"))
    implementation("org.springframework:spring-core")
    implementation("org.springframework.shell:spring-shell-core")
    compileOnly("org.springframework.shell:spring-shell-autoconfigure")
}
