package dev.tronto.issuereports.ep14.starter

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy
import ch.qos.logback.core.spi.ScanException
import ch.qos.logback.core.util.FileSize
import ch.qos.logback.core.util.OptionHelper
import org.springframework.boot.logging.LogFile
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.CorrelationIdConverter
import org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter
import java.nio.charset.Charset

internal class DefaultLogbackConfiguration(private val logFile: LogFile?) {
    fun apply(config: LogbackConfigurator) {
        synchronized(config.configurationLock) {
            defaults(config)
            @Suppress("UNUSED_VARIABLE")
            val consoleAppender = consoleAppender(config)
            if (this.logFile != null) {
                val fileAppender =
                    fileAppender(config, logFile.toString())
                config.root(Level.INFO, consoleAppender, fileAppender)
            } else {
                config.root(Level.INFO, consoleAppender)
            }
        }
    }

    private fun defaults(config: LogbackConfigurator) {
        config.conversionRule("clr", ColorConverter::class.java)
        config.conversionRule("correlationId", CorrelationIdConverter::class.java)
        config.conversionRule("wex", WhitespaceThrowableProxyConverter::class.java)
        config.conversionRule("wEx", ExtendedWhitespaceThrowableProxyConverter::class.java)
        config.context
            .putProperty(
                "CONSOLE_LOG_PATTERN",
                resolve(
                    config,
                    "\${CONSOLE_LOG_PATTERN:-" +
                        "%clr(%d{\${LOG_DATEFORMAT_PATTERN:-yyyy-MM-dd'T'HH:mm:ss.SSSXXX}}){faint} " +
                        "%clr(\${LOG_LEVEL_PATTERN:-%5p}) " +
                        "%clr(\${PID:- }){magenta} %clr(---){faint} " +
                        "%clr(\${LOGGED_APPLICATION_NAME:-}[%15.15t]){faint} " +
                        "%clr(\${LOG_CORRELATION_PATTERN:-}){faint}%clr(%-40.40logger{39}){cyan} " +
                        "%clr(:){faint} %m%n\${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"
                )
            )
        val defaultCharset = Charset.defaultCharset().name()
        config.context
            .putProperty("CONSOLE_LOG_CHARSET", resolve(config, "\${CONSOLE_LOG_CHARSET:-$defaultCharset}"))
        config.context.putProperty(
            "CONSOLE_LOG_THRESHOLD",
            resolve(config, "\${CONSOLE_LOG_THRESHOLD:-TRACE}")
        )
        config.context
            .putProperty(
                "FILE_LOG_PATTERN",
                resolve(
                    config,
                    "\${FILE_LOG_PATTERN:-" +
                        "%d{\${LOG_DATEFORMAT_PATTERN:-yyyy-MM-dd'T'HH:mm:ss.SSSXXX}} " +
                        "\${LOG_LEVEL_PATTERN:-%5p} \${PID:- } --- " +
                        "\${LOGGED_APPLICATION_NAME:-}[%t] " +
                        "\${LOG_CORRELATION_PATTERN:-}" +
                        "%-40.40logger{39} : %m%n\${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"
                )
            )
        config.context
            .putProperty("FILE_LOG_CHARSET", resolve(config, "\${FILE_LOG_CHARSET:-$defaultCharset}"))
        config.context.putProperty("FILE_LOG_THRESHOLD", resolve(config, "\${FILE_LOG_THRESHOLD:-TRACE}"))
        config.logger("org.apache.catalina.startup.DigesterFactory", Level.ERROR)
        config.logger("org.apache.catalina.util.LifecycleBase", Level.ERROR)
        config.logger("org.apache.coyote.http11.Http11NioProtocol", Level.WARN)
        config.logger("org.apache.sshd.common.util.SecurityUtils", Level.WARN)
        config.logger("org.apache.tomcat.util.net.NioSelectorPool", Level.WARN)
        config.logger("org.eclipse.jetty.util.component.AbstractLifeCycle", Level.ERROR)
        config.logger("org.hibernate.validator.internal.util.Version", Level.WARN)
        config.logger("org.springframework.boot.actuate.endpoint.jmx", Level.WARN)
    }

    private fun consoleAppender(config: LogbackConfigurator): Appender<ILoggingEvent> {
        val appender = JLineConsoleAppender<ILoggingEvent>()
        val filter = ThresholdFilter()
        filter.setLevel(resolve(config, "\${CONSOLE_LOG_THRESHOLD}"))
        filter.start()
        appender.addFilter(filter)
        val encoder = PatternLayoutEncoder()
        encoder.pattern = resolve(config, "\${CONSOLE_LOG_PATTERN}")
        encoder.charset = resolveCharset(config, "\${CONSOLE_LOG_CHARSET}")
        config.start(encoder)
        appender.encoder = encoder
        appender.charset = encoder.charset
        config.appender("CONSOLE", appender)
        return appender
    }

    private fun fileAppender(config: LogbackConfigurator, logFile: String): Appender<ILoggingEvent> {
        val appender = RollingFileAppender<ILoggingEvent>()
        val filter = ThresholdFilter()
        filter.setLevel(resolve(config, "\${FILE_LOG_THRESHOLD}"))
        filter.start()
        appender.addFilter(filter)
        val encoder = PatternLayoutEncoder()
        encoder.pattern = resolve(config, "\${FILE_LOG_PATTERN}")
        encoder.charset = resolveCharset(config, "\${FILE_LOG_CHARSET}")
        appender.encoder = encoder
        config.start(encoder)
        appender.file = logFile
        setRollingPolicy(appender, config)
        config.appender("FILE", appender)
        return appender
    }

    private fun setRollingPolicy(appender: RollingFileAppender<ILoggingEvent>, config: LogbackConfigurator) {
        val rollingPolicy = SizeAndTimeBasedRollingPolicy<ILoggingEvent>()
        rollingPolicy.context = config.context
        rollingPolicy.fileNamePattern =
            resolve(config, "\${LOGBACK_ROLLINGPOLICY_FILE_NAME_PATTERN:-\${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz}")
        rollingPolicy.isCleanHistoryOnStart =
            resolveBoolean(config, "\${LOGBACK_ROLLINGPOLICY_CLEAN_HISTORY_ON_START:-false}")
        rollingPolicy.setMaxFileSize(resolveFileSize(config, "\${LOGBACK_ROLLINGPOLICY_MAX_FILE_SIZE:-10MB}"))
        rollingPolicy.setTotalSizeCap(resolveFileSize(config, "\${LOGBACK_ROLLINGPOLICY_TOTAL_SIZE_CAP:-0}"))
        rollingPolicy.maxHistory = resolveInt(config, "\${LOGBACK_ROLLINGPOLICY_MAX_HISTORY:-7}")
        appender.rollingPolicy = rollingPolicy
        rollingPolicy.setParent(appender)
        config.start(rollingPolicy)
    }

    private fun resolveBoolean(config: LogbackConfigurator, `val`: String): Boolean = resolve(config, `val`).toBoolean()

    private fun resolveInt(config: LogbackConfigurator, `val`: String): Int = resolve(config, `val`).toInt()

    private fun resolveFileSize(config: LogbackConfigurator, `val`: String): FileSize =
        FileSize.valueOf(resolve(config, `val`))

    private fun resolveCharset(config: LogbackConfigurator, `val`: String): Charset =
        Charset.forName(resolve(config, `val`))

    private fun resolve(config: LogbackConfigurator, `val`: String): String {
        try {
            return OptionHelper.substVars(`val`, config.context)
        } catch (ex: ScanException) {
            throw RuntimeException(ex)
        }
    }
}
