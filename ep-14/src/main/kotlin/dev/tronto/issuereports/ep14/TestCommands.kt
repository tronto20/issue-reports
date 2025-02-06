package dev.tronto.issuereports.ep14

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.shell.ExitRequest
import org.springframework.shell.command.annotation.Command

@Command
class TestCommands(
    private val applicationContext: ApplicationContext,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Command(command = ["hello"])
    fun hello() {
        logger.info("Hello world!")
    }

    @Command(command = ["exit"])
    fun exit() {
        SpringApplication.exit(applicationContext)
        throw ExitRequest()
    }
}
