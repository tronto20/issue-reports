package dev.tronto.issuereports.ep14

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class LoggingScheduler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelay = 1000)
    fun test() {
        logger.info("JLine test")
    }
}
