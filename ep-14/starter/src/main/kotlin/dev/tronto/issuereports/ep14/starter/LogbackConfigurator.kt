package dev.tronto.issuereports.ep14.starter

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.CoreConstants
import ch.qos.logback.core.pattern.Converter
import ch.qos.logback.core.spi.ContextAware
import ch.qos.logback.core.spi.LifeCycle
import org.slf4j.Logger
import org.springframework.util.Assert

/**
 * Allows programmatic configuration of logback which is usually faster than parsing XML.
 *
 * @author Phillip Webb
 */
internal open class LogbackConfigurator(val context: LoggerContext) {
    val configurationLock: Any
        get() = context.configurationLock

    open fun conversionRule(conversionWord: String, converterClass: Class<out Converter<*>?>) {
        Assert.hasLength(conversionWord, "Conversion word must not be empty")
        Assert.notNull(converterClass, "Converter class must not be null")
        @Suppress("UNCHECKED_CAST")
        var registry =
            context
                .getObject(CoreConstants.PATTERN_RULE_REGISTRY) as? MutableMap<String?, String?>
        if (registry == null) {
            registry = HashMap()
            context.putObject(CoreConstants.PATTERN_RULE_REGISTRY, registry)
        }
        registry[conversionWord] = converterClass.name
    }

    open fun appender(name: String, appender: Appender<*>) {
        appender.name = name
        start(appender)
    }

    @JvmOverloads
    fun logger(name: String, level: Level, additive: Boolean = true) {
        logger(name, level, additive, null)
    }

    open fun logger(name: String, level: Level?, additive: Boolean, appender: Appender<ILoggingEvent>?) {
        val logger = context.getLogger(name)
        if (level != null) {
            logger.level = level
        }
        logger.isAdditive = additive
        if (appender != null) {
            logger.addAppender(appender)
        }
    }

    @SafeVarargs
    fun root(level: Level?, vararg appenders: Appender<ILoggingEvent>) {
        val logger = context.getLogger(Logger.ROOT_LOGGER_NAME)
        if (level != null) {
            logger.level = level
        }
        for (appender in appenders) {
            logger.addAppender(appender)
        }
    }

    open fun start(lifeCycle: LifeCycle) {
        if (lifeCycle is ContextAware) {
            lifeCycle.context = context
        }
        lifeCycle.start()
    }
}
