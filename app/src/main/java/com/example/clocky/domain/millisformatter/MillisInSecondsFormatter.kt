package com.example.clocky.domain.millisformatter

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * A concrete implementation of [MillisFormatter] that converts the time stamp in millis
 * to a string in the following format - "HH:mm:ss"
 * Eg : "01:02:30"
 */
class MillisInSecondsFormatter : MillisFormatter {
    /**
     * The date time formatter used to format the millis.
     */
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    /**
     * Formats the given [millis] as a [String] using the "HH:mm:ss" pattern.
     */
    override fun formatMillis(millis: Long, timeZoneId: String): String = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(millis),
        ZoneId.of(timeZoneId)
    ).toLocalTime().format(dateTimeFormatter)
}