package dev.tronto.issuereports.ep14

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.shell.command.annotation.EnableCommand

@SpringBootApplication
@EnableScheduling
@EnableCommand(TestCommands::class)
class Ep14Application

fun main() {
    runApplication<Ep14Application>()
}
