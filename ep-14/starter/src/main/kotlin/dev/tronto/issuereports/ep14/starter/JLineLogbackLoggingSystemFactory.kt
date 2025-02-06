package dev.tronto.issuereports.ep14.starter

import org.springframework.boot.logging.LoggingSystem
import org.springframework.boot.logging.LoggingSystemFactory
import org.springframework.util.ClassUtils

class JLineLogbackLoggingSystemFactory : LoggingSystemFactory {
    override fun getLoggingSystem(classLoader: ClassLoader): LoggingSystem? {
        if (PRESENT) {
            return JLineLogbackLoggingSystem(classLoader)
        }
        return null
    }

    companion object {
        private val PRESENT = ClassUtils.isPresent(
            "ch.qos.logback.classic.LoggerContext",
            JLineLogbackLoggingSystemFactory::class.java.classLoader
        ) && ClassUtils.isPresent(
            "org.jline.reader.LineReader",
            JLineLogbackLoggingSystemFactory::class.java.classLoader
        )
    }
}
