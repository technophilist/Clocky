package com.example.clocky.ui.stopwatch

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.clocky.domain.stopwatch.ClockyStopwatch
import com.example.clocky.domain.stopwatch.Stopwatch

/**
 * A service that provides a stopwatch.
 */
class StopwatchService : Service() {
    // todo inject stopwatch
    private val stopwatch = ClockyStopwatch()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_NOT_STICKY

    override fun onBind(intent: Intent): IBinder = StopwatchServiceBinder()

    /**
     * A binder that provides access to the stopwatch instance used by this service.
     *
     * @property stopwatch The stopwatch instance used by this service.
     */
    inner class StopwatchServiceBinder : Binder() {
        val stopwatch = this@StopwatchService.stopwatch as Stopwatch
    }
}
/*

class StopwatchService : Service() {
    val stopwatch = ClockyStopwatch()



}
 */