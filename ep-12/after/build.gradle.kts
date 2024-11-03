import org.gradle.configurationcache.extensions.capitalized
import org.jmailen.gradle.kotlinter.tasks.LintTask
import org.springframework.boot.gradle.tasks.buildinfo.BuildInfo
import org.springframework.boot.gradle.tasks.bundling.BootArchive
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    issuereports
    kotlin("plugin.spring")
    kotlin("jvm")
    id("org.springframework.boot")
    id("org.graalvm.buildtools.native")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(projects.dependencies))
    implementation("org.springframework.boot:spring-boot-starter")
}

/**
 *  genSourceSet 생성, 리소스 위치 설정
 */
val genResourceDir = layout.buildDirectory.dir("generated/genResources")
val genSourceSet = sourceSets.create("gen") {
    resources.srcDir(genResourceDir)
}

project.pluginManager.withPlugin("org.springframework.boot") {
    /**
     *  bootJar 에 genSourceSet 도 포함하도록 설정
     */
    tasks.withType<BootArchive>() {
        dependsOn(genSourceSet.classesTaskName)
        classpath(genSourceSet.runtimeClasspath)
    }

    /**
     *  gen SourceSet 을 포함하는 bootRun task 를 추가.
     */
    tasks.create<BootRun>("bootGenRun") {
        group = "application"
        description = "bootRun with gen sourceSet"
        dependsOn(genSourceSet.classesTaskName)
        classpath(genSourceSet.runtimeClasspath)
        val bootRun = tasks.withType<BootRun>().getByName("bootRun")
        mainClass.convention(bootRun.mainClass)
        classpath(bootRun.classpath)
        javaLauncher.convention(bootRun.javaLauncher)
    }

    /**
     *  bootBuildInfo task 설정
     */
    springBoot {
        buildInfo {
            destinationDir.set(genResourceDir)
            tasks.getByName(genSourceSet.processResourcesTaskName).dependsOn(this)
        }
        tasks.named(JavaPlugin.CLASSES_TASK_NAME).configure {
            setDependsOn(dependsOn.filterNot { it is BuildInfo })
        }
    }

    tasks.build {
        doLast {
            if (tasks.compileKotlin.get().state.skipped && tasks.compileJava.get().state.skipped) {
                if (!tasks.processAot.get().state.skipped) {
                    throw IllegalStateException(
                        "코드 컴파일은 스킵되었으나 processAot 가 스킵되지 않았습니다. " +
                                "이는 build Task 가 아닌 개별 Task 를 실행했을 경우에도 발생할 수 있습니다. " +
                                "만약 그렇다면 clean build --no-build-cache 를 실행해야 합니다."
                    )
                }
            }
        }
    }
}

/**
 *  Aot, Gen sourceSet 의 lint, format 제거
 */
pluginManager.withPlugin("org.jmailen.kotlinter") {
    val ignoreSourceSets = listOf("aot", "aotTest", "gen")
    afterEvaluate {
        tasks.lintKotlin {
            val ignoreLintSourceSets = ignoreSourceSets.map { "lintKotlin${it.replaceFirstChar(Char::titlecase)}" }
            setDependsOn(dependsOn.filter {
                val task: Task = (
                        if (it is Provider<*>) it.orNull
                        else it
                        ) as? Task ?: return@filter true
                task.name !in ignoreLintSourceSets
            })
        }
        tasks.formatKotlin {
            val ignoreFormatSourceSets = ignoreSourceSets.map { "formatKotlin${it.replaceFirstChar(Char::titlecase)}" }
            setDependsOn(dependsOn.filter {
                val task: Task = (
                        if (it is Provider<*>) it.orNull
                        else it
                        ) as? Task ?: return@filter true
                task.name !in ignoreFormatSourceSets
            })
        }
        tasks.compileKotlin {
            dependsOn(tasks.formatKotlin)
        }
    }

    /**
     *  실제로 실행되지 않았는지 확인.
     */
    tasks.build {
        doLast {
            if (tasks.lintKotlin.get().state.executed) {
                val anyExecuted = ignoreSourceSets.map { "lintKotlin${it.replaceFirstChar(Char::titlecase)}" }
                    .map { tasks.getByName(it) }
                    .filter { it.state.executed }

                if (anyExecuted.isNotEmpty()) {
                    throw IllegalStateException("실행되지 않도록 설정했지만 실행된 Task 가 있습니다: $anyExecuted")
                }
            }
            if (tasks.formatKotlin.get().state.executed) {
                val anyExecuted = ignoreSourceSets.map { "formatKotlin${it.replaceFirstChar(Char::titlecase)}" }
                    .map { tasks.getByName(it) }
                    .filter { it.state.executed }

                if (anyExecuted.isNotEmpty()) {
                    throw IllegalStateException("실행되지 않도록 설정했지만 실행된 Task 가 있습니다: $anyExecuted")
                }
            }
        }
    }

}

