package com.example.clocky.domain.stopwatch

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * An implementation of [Stopwatch].
 */
class ClockyStopwatch(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : Stopwatch {
    private val _millisElapsedStream = MutableStateFlow(0L)
    private var isPaused = true
    override val millisElapsedStream = _millisElapsedStream.asStateFlow()

    override suspend fun start() {
        withContext(defaultDispatcher) {
            isPaused = false
            while (!isPaused && isActive) {
                _millisElapsedStream.value = _millisElapsedStream.value + 1
                delay(1)
            }
        }
    }

    override fun pause() {
        isPaused = true
    }

    override fun reset() {
        isPaused = true
        _millisElapsedStream.value = 0L
    }
}