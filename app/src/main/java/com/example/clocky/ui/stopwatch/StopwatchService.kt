package com.example.clocky.ui.stopwatch

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.clocky.di.ClockyApplication
import com.example.clocky.domain.millisformatter.MillisFormatter
import com.example.clocky.domain.stopwatch.Stopwatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * A service that provides a stopwatch.
 */
class StopwatchService : Service() {
    private val stopwatchServiceBinder = StopwatchServiceBinder()
    private val _stopwatchState = MutableStateFlow(StopwatchState.RESET)
    private lateinit var stopwatch: Stopwatch
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var millisFormatter: MillisFormatter
    lateinit var formattedElapsedMillisStream: Flow<String>
    val stopwatchState = _stopwatchState as Flow<StopwatchState>


    override fun onCreate() {
        with((application as ClockyApplication).getServiceContainer()) {
            stopwatch = provideStopwatch()
            coroutineScope = provideCoroutineScope()
            millisFormatter = provideMillisFormatter()
            formattedElapsedMillisStream = stopwatch
                .millisElapsedStream
                .map(millisFormatter::formatMillis)
        }
    }

    fun startStopwatch() {
        _stopwatchState.value = StopwatchState.RUNNING
        coroutineScope.launch {
            stopwatch.start()
        }
    }

    fun pauseStopwatch() {
        stopwatch.pause()
        _stopwatchState.value = StopwatchState.PAUSED
    }

    fun stopAndResetStopwatch() {
        stopwatch.reset()
        _stopwatchState.value = StopwatchState.RESET
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_NOT_STICKY

    override fun onBind(intent: Intent): IBinder = stopwatchServiceBinder
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    /**
     * A binder that provides access to the stopwatch instance used by this service.
     *
     * @property stopwatch The stopwatch instance used by this service.
     */
    inner class StopwatchServiceBinder : Binder() {
        val service: StopwatchService = this@StopwatchService
    }

    /**
     * An enum representing the different states of the stopwatch.
     */
    enum class StopwatchState { RESET, PAUSED, RUNNING }
}