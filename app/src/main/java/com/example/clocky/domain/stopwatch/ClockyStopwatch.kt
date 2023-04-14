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

    /**
     * This method is responsible for starting the stopwatch.
     *
     * The method works in the following manner:
     * Initially one might think that the elapsed time could be easily computed by using the
     * [delay] method, as follows:
     * ```
     * private val _millisElapsedStream = MutableStateFlow(0L)
     *
     * suspend fun start() {
     *   withContext(defaultDispatcher) {
     *          isPaused = false
     *          // after every 1ms add one to the current value and update the flow
     *           while (!isPaused && isActive) { // line 0
     *              _millisElapsedStream.value = _millisElapsedStream.value + 1 // line 1
     *              delay(1) // line 2
     *          }
     *      }
     *  }
     * ```
     * But, this has a big inherent problem associated with it.
     * Since the computation of the elapsed time is done in a background thread, there is no
     * control over the execution of the code that is being executed. For example, the cpu might be
     * done with "line 2" but may decide to work on another coroutine before coming back to this
     * coroutine. In which case, the number of millis elapsed might be more than 1 millis. When it
     * resumes back at "line 0", the value of the stream is incremented by 1. But this is incorrect
     * because the actual number of millis elapsed since the last emission was more than 1 millis
     * (Because the scheduler decided to work on another coroutine). To circumvent this issue,
     * we can leverage [System.currentTimeMillis].
     * By saving the timestamp of the last emission, it becomes possible to calculate the millis
     * difference between the currentTimeStamp (the timestamp that is about to be emitted) and the
     * timestamp of the last emission. This way, we would know the exact number of milliseconds
     * that has elapsed after every iteration of the loop.
     * @see [Stopwatch.start]
     */
    override suspend fun start() {
        withContext(defaultDispatcher) {
            isPaused = false
            var timeStampOfLastEmission = System.currentTimeMillis()
            while (!isPaused && isActive) {
                val currentTimeStamp = System.currentTimeMillis()
                val differenceInMillis = currentTimeStamp - timeStampOfLastEmission
                _millisElapsedStream.value = _millisElapsedStream.value + differenceInMillis
                timeStampOfLastEmission = currentTimeStamp
                delay(10)
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