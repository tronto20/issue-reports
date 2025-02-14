package dev.tronto.issuereports.ep14.starter

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.pattern.Converter
import ch.qos.logback.core.spi.LifeCycle
import ch.qos.logback.core.status.InfoStatus

/**
 * Custom [LogbackConfigurator] used to add [Statuses][Status] when Logback
 * debugging is enabled.
 *
 * @author Andy Wilkinson
 */
internal class DebugLogbackConfigurator(context: LoggerContext) : LogbackConfigurator(context) {
    override fun conversionRule(conversionWord: String, converterClass: Class<out Converter<*>?>) {
        info("Adding conversion rule of type '" + converterClass.name + "' for word '" + conversionWord + "'")
        super.conversionRule(conversionWord, converterClass)
    }

    override fun appender(name: String, appender: Appender<*>) {
        info("Adding appender '$appender' named '$name'")
        super.appender(name, appender)
    }

    override fun logger(name: String, level: Level?, additive: Boolean, appender: Appender<ILoggingEvent>?) {
        info("Configuring logger '$name' with level '$level'. Additive: $additive")
        if (appender != null) {
            info("Adding appender '$appender' to logger '$name'")
        }
        super.logger(name, level, additive, appender)
    }

    override fun start(lifeCycle: LifeCycle) {
        info("Starting '$lifeCycle'")
        super.start(lifeCycle)
    }

    private fun info(message: String) {
        context.statusManager.add(InfoStatus(message, this))
    }
}
