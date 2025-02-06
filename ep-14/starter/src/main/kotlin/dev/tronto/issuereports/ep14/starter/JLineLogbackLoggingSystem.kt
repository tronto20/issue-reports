package dev.tronto.issuereports.ep14.starter

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.jul.LevelChangePropagator
import ch.qos.logback.classic.turbo.TurboFilter
import ch.qos.logback.core.spi.FilterReply
import ch.qos.logback.core.status.OnConsoleStatusListener
import ch.qos.logback.core.util.StatusListenerConfigHelper
import org.slf4j.ILoggerFactory
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.bridge.SLF4JBridgeHandler
import org.slf4j.helpers.SubstituteLoggerFactory
import org.springframework.boot.logging.LogFile
import org.springframework.boot.logging.LoggingInitializationContext
import org.springframework.boot.logging.logback.LogbackLoggingSystem
import org.springframework.boot.logging.logback.LogbackLoggingSystemProperties
import org.springframework.util.Assert
import org.springframework.util.ClassUtils
import java.util.logging.LogManager

class JLineLogbackLoggingSystem(classLoader: ClassLoader) : LogbackLoggingSystem(classLoader) {
    private fun getLoggerFactory(): ILoggerFactory {
        var factory = LoggerFactory.getILoggerFactory()
        while (factory is SubstituteLoggerFactory) {
            try {
                Thread.sleep(50)
            } catch (ex: InterruptedException) {
                Thread.currentThread().interrupt()
                throw IllegalStateException("Interrupted while waiting for non-substitute logger factory", ex)
            }
            factory = LoggerFactory.getILoggerFactory()
        }
        return factory
    }

    private fun getLocation(factory: ILoggerFactory): Any {
        try {
            val protectionDomain = factory.javaClass.protectionDomain
            val codeSource = protectionDomain.codeSource
            if (codeSource != null) {
                return codeSource.location
            }
        } catch (ex: SecurityException) {
            // Unable to determine location
        }
        return "unknown location"
    }

    private fun getLoggerContext(): LoggerContext {
        val factory = getLoggerFactory()
        Assert.isInstanceOf(
            LoggerContext::class.java,
            factory
        ) {
            String.format(
                "LoggerFactory is not a Logback LoggerContext but Logback is on " +
                    "the classpath. Either remove Logback or the competing " +
                    "implementation (%s loaded from %s). If you are using " +
                    "WebLogic you will need to add 'org.slf4j' to " +
                    "prefer-application-packages in WEB-INF/weblogic.xml",
                factory.javaClass,
                getLocation(factory)
            )
        }
        return factory as LoggerContext
    }

    override fun loadDefaults(initializationContext: LoggingInitializationContext, logFile: LogFile?) {
        val context = getLoggerContext()
        stopAndReset(context)
        withLoggingSuppressed(
            Runnable {
                val debug = System.getProperty("logback.debug").toBoolean()
                if (debug) {
                    StatusListenerConfigHelper.addOnConsoleListenerInstance(context, OnConsoleStatusListener())
                }
                val environment = initializationContext.environment
                // Apply system properties directly in case the same JVM runs multiple apps
                LogbackLoggingSystemProperties(
                    environment,
                    getDefaultValueResolver(environment)
                ) { key: String?, `val`: String? ->
                    context.putProperty(
                        key,
                        `val`
                    )
                }.apply(logFile)
                val configurator =
                    if (debug) {
                        DebugLogbackConfigurator(context)
                    } else {
                        LogbackConfigurator(context)
                    }
                DefaultLogbackConfiguration(logFile).apply(configurator)
                context.isPackagingDataEnabled = true
            }
        )
    }

    private fun withLoggingSuppressed(action: Runnable) {
        val turboFilters = getLoggerContext().turboFilterList
        turboFilters.add(FILTER)
        try {
            action.run()
        } finally {
            turboFilters.remove(FILTER)
        }
    }

    private fun stopAndReset(loggerContext: LoggerContext) {
        loggerContext.stop()
        loggerContext.reset()
        if (isBridgeHandlerInstalled()) {
            addLevelChangePropagator(loggerContext)
        }
    }

    private fun isBridgeHandlerInstalled(): Boolean {
        if (!isBridgeHandlerAvailable()) {
            return false
        }
        val rootLogger = LogManager.getLogManager().getLogger("")
        val handlers = rootLogger.handlers
        return handlers.size == 1 && handlers[0] is SLF4JBridgeHandler
    }

    private fun addLevelChangePropagator(loggerContext: LoggerContext) {
        val levelChangePropagator = LevelChangePropagator()
        levelChangePropagator.setResetJUL(true)
        levelChangePropagator.context = loggerContext
        loggerContext.addListener(levelChangePropagator)
    }

    private fun isBridgeHandlerAvailable(): Boolean = ClassUtils.isPresent(BRIDGE_HANDLER, classLoader)

    companion object {
        private const val BRIDGE_HANDLER = "org.slf4j.bridge.SLF4JBridgeHandler"
        private val FILTER: TurboFilter =
            object : TurboFilter() {
                override fun decide(
                    marker: Marker?,
                    logger: Logger?,
                    level: Level?,
                    format: String?,
                    params: Array<Any>?,
                    t: Throwable?,
                ): FilterReply = FilterReply.DENY
            }
    }
}
