package com.example.clocky.domain.stopwatch

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * An implementation of [Stopwatch].
 */
class ClockyStopwatch(
    private val intervalMillis: Long = 1,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : Stopwatch {
    private val _currentMillisStream = MutableStateFlow(0L)
    private var isPaused = true
    override val currentMillisStream = _currentMillisStream.asStateFlow()

    override suspend fun start() {
        withContext(defaultDispatcher) {
            isPaused = false
            while (!isPaused) {
                _currentMillisStream.value = _currentMillisStream.value + 1
                delay(intervalMillis)
            }
        }
    }

    override fun pause() {
        isPaused = true
    }

    override fun reset() {
        isPaused = true
        _currentMillisStream.value = 0L
    }
}