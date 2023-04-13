package com.example.clocky.domain.stopwatch

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class ClockyStopwatchTest {

    private lateinit var stopwatch: Stopwatch

    @Before
    fun setup() {
        stopwatch = ClockyStopwatch()
    }

    @Test
    fun `Running the stopwatch for 500 millis should update the mills stream correctly`() =
        runTest {
            val millisEmissions = mutableListOf<Long>()
            with(backgroundScope) {
                launch { stopwatch.currentMillisStream.toList(millisEmissions) }
                // given a stop watch that runs for 500 millis
                launch { stopwatch.start() }
            }
            advanceTimeBy(500)
            stopwatch.pause()
            // the emissions must contain all values from [0 - 500] inclusive
            assert(millisEmissions == (0..500L).toList())
        }

    @Test
    fun `Correct values are emitted when pausing and restarting the stopwatch`() = runTest {
        val millisEmissions = mutableListOf<Long>()
        with(backgroundScope) {
            launch { stopwatch.currentMillisStream.toList(millisEmissions) }
            launch { stopwatch.start() }
        }
        // when the stopwatch runs for 250ms and gets paused
        advanceTimeBy(250)
        stopwatch.pause()
        // the last emitted value must be 250
        assert(millisEmissions.last() == 250L)
        // when the stopwatch is re-started and run for another 250ms
        backgroundScope.launch { stopwatch.start() }
        advanceTimeBy(250)
        // the last emitted value must be 500
        assert(millisEmissions.last() == 500L)
    }

    @Test
    fun `Resetting stopwatch restarts from 0`() = runTest {
        val millisEmissions = mutableListOf<Long>()
        with(backgroundScope) {
            // todo need to understand why this test fails without UnConfinedDispatcher()
            launch(UnconfinedTestDispatcher()) {
                stopwatch.currentMillisStream.toList(millisEmissions)
            }
            launch { stopwatch.start() }
        }
        // when the stopwatch runs for 500ms and gets reset
        advanceTimeBy(500)
        stopwatch.reset()
        // the last emitted value should be 0
        assert(millisEmissions.last() == 0L)
        // when it is started again and run for 500ms
        backgroundScope.launch { stopwatch.start() }
        advanceTimeBy(500)
        stopwatch.pause()
        // the last emitted value must be 500 because the timer was restarted
        assert(millisEmissions.last() == 500L)
    }

}

