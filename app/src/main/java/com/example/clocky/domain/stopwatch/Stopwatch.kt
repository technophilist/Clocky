package com.example.clocky.domain.stopwatch

import kotlinx.coroutines.flow.Flow

/**
 * A stopwatch interface that provides functionality to start, pause and reset the stopwatch.
 * It also provides a flow of current milliseconds elapsed since the stopwatch started.
 */
interface Stopwatch {
    /**
     * A flow of current milliseconds elapsed since the stopwatch started.
     */
    val currentMillisStream: Flow<Long>

    /**
     * Starts the stopwatch. Please note that **this method suspends until the stopwatch is
     * paused/reset.**
     */
    suspend fun start()

    /**
     * Pauses the stopwatch.
     */
    fun pause()

    /**
     * Resets the stopwatch to zero.
     */
    fun reset()
}