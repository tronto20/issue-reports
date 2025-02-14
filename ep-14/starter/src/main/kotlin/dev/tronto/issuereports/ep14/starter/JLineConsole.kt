package dev.tronto.issuereports.ep14.starter

import org.jline.reader.LineReader
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.shell.boot.LineReaderAutoConfiguration

internal object JLineConsole : JLineReaderProvider {
    private var lineReader: LineReader? = null
    override fun lineReader(): LineReader? {
        return lineReader
    }

    @AutoConfiguration(after = [LineReaderAutoConfiguration::class])
    @ConditionalOnBean(LineReader::class)
    class LineReaderLoggingAutoConfiguration(
        lineReader: LineReader,
    ) {
        init {
            JLineConsole.lineReader = lineReader
        }
    }
}
