package com.example.clocky.domain.millisformatter

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * An implementation of [MillisFormatter] that formats time in the "HH:mm:ss:SS" pattern.
 */
class ClockyMillisFormatter : MillisFormatter {
    /**
     * The date time formatter used to format the millis.
     */
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS")

    /**
     * Formats the given [millis] as a [String] using the "HH:mm:ss:SS" pattern.
     * Please note that this method uses "Etc/GMT" as the timezone for the conversion.
     */
    override fun formatMillis(millis: Long): String = LocalDateTime
        .ofInstant(Instant.ofEpochMilli(millis), ZoneId.of("Etc/GMT"))
        .toLocalTime()
        .format(dateTimeFormatter)
}