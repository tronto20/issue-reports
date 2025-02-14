package dev.tronto.issuereports.ep14.starter

import org.jline.reader.LineReader

fun interface JLineReaderProvider {
    fun lineReader(): LineReader?
}
