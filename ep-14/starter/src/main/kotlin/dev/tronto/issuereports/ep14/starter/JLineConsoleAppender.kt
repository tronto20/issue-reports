package dev.tronto.issuereports.ep14.starter

import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.Layout
import java.nio.charset.Charset

class JLineConsoleAppender<E>(
    private val jLineReaderProvider: JLineReaderProvider = JLineConsole,
) : ConsoleAppender<E>() {
    var charset: Charset = Charset.defaultCharset()
    private var layout: Layout<E>? = null
    private val lineReader
        get() = jLineReaderProvider.lineReader()

    override fun setLayout(layout: Layout<E>?) {
        this.layout = layout
        super.setLayout(layout)
    }

    override fun append(event: E?) {
        val lineReader = this.lineReader
        if (lineReader != null) {
            val layout = this.layout
            val encoder = this.encoder
            if (layout != null) {
                lineReader.printAbove(layout.doLayout(event))
            } else if (encoder != null) {
                lineReader.printAbove(encoder.encode(event).toString(charset))
            } else {
                super.append(event)
            }
        } else {
            super.append(event)
        }
    }
}
