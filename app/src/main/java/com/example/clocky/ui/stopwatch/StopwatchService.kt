package com.example.clocky.ui.stopwatch

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.clocky.di.ClockyApplication
import com.example.clocky.domain.stopwatch.ClockyStopwatch
import com.example.clocky.domain.stopwatch.Stopwatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * A service that provides a stopwatch.
 */
class StopwatchService : Service() {
    private lateinit var stopwatch: Stopwatch
    private lateinit var coroutineScope: CoroutineScope
    private val stopwatchServiceBinder = StopwatchServiceBinder()

    override fun onCreate() {
        with((application as ClockyApplication).getServiceContainer()) {
            stopwatch = provideStopwatch()
            coroutineScope = provideCoroutineScope()
        }
    }

    fun startStopwatch() {
        // todo handle case where this method is called multiple times by different threads
        coroutineScope.launch {
            stopwatch.start()
        }
    }

    fun pauseStopwatch() {
        stopwatch.pause()
    }

    fun stopAndResetStopwatch() {
        stopwatch.reset()
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
        val stopwatch = this@StopwatchService.stopwatch as Stopwatch
    }
}