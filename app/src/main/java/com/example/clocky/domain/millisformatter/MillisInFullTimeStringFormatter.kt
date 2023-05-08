package com.example.clocky.domain.millisformatter

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * An implementation of [MillisFormatter] that formats time in the "HH:mm:ss:SS" pattern.
 */
class MillisInFullTimeStringFormatter : MillisFormatter {
    /**
     * The date time formatter used to format the millis.
     */
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS")

    /**
     * Formats the given [millis] as a [String] using the "HH:mm:ss:SS" pattern.
     */
    override fun formatMillis(millis: Long, timeZoneId: String): String = LocalDateTime
        .ofInstant(Instant.ofEpochMilli(millis), ZoneId.of(timeZoneId))
        .toLocalTime()
        .format(dateTimeFormatter)
}