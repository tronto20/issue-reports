plugins {
    kotlin("jvm") apply false
    id("org.graalvm.buildtools.native") apply false
    issuereports
}

allprojects {
    group = "dev.tronto.issuereports"
}
