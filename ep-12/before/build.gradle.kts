plugins {
    issuereports
    kotlin("plugin.spring")
    kotlin("jvm")
    id("org.springframework.boot")
    id("org.graalvm.buildtools.native")
    id("com.epages.restdocs-api-spec")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(projects.dependencies))
    implementation("org.springframework.boot:spring-boot-starter")
}

pluginManager.withPlugin("org.springframework.boot") {
    springBoot {
        buildInfo()
    }

    tasks.build {
        doLast {
            if (tasks.compileKotlin.get().state.skipped && tasks.compileJava.get().state.skipped) {
                if (tasks.processAot.get().state.skipped) {
                    throw IllegalStateException(
                        "코드 컴파일은 스킵되어도 processAot 가 스킵되지 않는 현상이 있어야 하지만 스킵되었습니다. " +
                                "이는 bootBuildInfo Task 를 설정하지 않았거나 업데이트로 인해 구성이 바뀌었을 수 있습니다."
                    )
                }
            }
        }
    }
}

pluginManager.withPlugin("org.jmailen.kotlinter") {
    val ignoreSourceSets = listOf("aot", "aotTest")
    /**
     *  현재 실행되고 있는지 확인.
     */
    tasks.build {
        doLast {
            if (tasks.lintKotlin.get().state.executed) {
                val anyExecuted = ignoreSourceSets.map { "lintKotlin${it.replaceFirstChar(Char::titlecase)}" }
                    .map { tasks.getByName(it) }
                    .filterNot { it.state.executed }

                if (anyExecuted.isNotEmpty()) {
                    throw IllegalStateException("실행되어야 하지만 실행되지 않은 Task 가 있습니다: $anyExecuted")
                }
            }
            if (tasks.formatKotlin.get().state.executed) {
                val anyExecuted = ignoreSourceSets.map { "formatKotlin${it.replaceFirstChar(Char::titlecase)}" }
                    .map { tasks.getByName(it) }
                    .filterNot { it.state.executed }

                if (anyExecuted.isNotEmpty()) {
                    throw IllegalStateException("실행되어야 하지만 실행되지 않은 Task 가 있습니다: $anyExecuted")
                }
            }
        }
    }

}


pluginManager.withPlugin("com.epages.restdocs-api-spec") {
    openapi.format = "yaml"
    afterEvaluate {
        val openapiTask = tasks.getByName("openapi")
        tasks.bootJar {
            dependsOn(openapiTask)
        }
    }


    tasks.build {
        doLast {
            val openapiPath = openapi.outputDirectory + "/" + openapi.outputFileNamePrefix + "." + openapi.format
            val processResources = tasks.processResources.get()
            if (processResources.inputs.files.map { it.path }.contains(openapiPath)) {
                throw IllegalStateException("openapi Task 의 output 이 processGenResources 의 input 으로 들어갑니다. 들어가지 않는 것을 예상했습니다.")
            }

            val bootJarTask = tasks.bootJar.get()
            if (bootJarTask.inputs.files.map { it.name }.contains(openapi.outputFileNamePrefix + "." + openapi.format)) {
                throw IllegalStateException("openapi Task 의 output 이 bootJar 의 input 으로 들어갑니다. 들어가지 않는 것을 예상했습니다.")
            }
        }
    }
}
