package com.example.clocky.domain.millisformatter

/**
 * A functional interface for formatting a given time (in milliseconds) as a [String].
 */
fun interface MillisFormatter {
    /**
     * Formats the given [millis]as a [String].
     */
    fun formatMillis(millis: Long): String
}