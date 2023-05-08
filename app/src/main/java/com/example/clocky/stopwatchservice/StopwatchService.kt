package com.example.clocky.stopwatchservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.example.clocky.di.ClockyApplication
import com.example.clocky.domain.millisformatter.MillisFormatter
import com.example.clocky.domain.stopwatch.Stopwatch
import com.example.clocky.stopwatchservice.notification.StopwatchNotificationBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * A service that manages a stopwatch.
 */
class StopwatchService : Service() {
    private val notificationManager by lazy {
        NotificationManagerCompat.from(this@StopwatchService)
    }
    private val stopwatchServiceBinder = StopwatchServiceBinder()
    private val _stopwatchState = MutableStateFlow(StopwatchState.RESET)
    private lateinit var stopwatch: Stopwatch
    private lateinit var serviceScope: CoroutineScope
    private lateinit var millisInTimeStringFormatter: MillisFormatter
    private lateinit var millisToSecondsFormatter: MillisFormatter
    private lateinit var notificationBuilder: StopwatchNotificationBuilder
    lateinit var formattedElapsedMillisStream: Flow<String>
    val stopwatchState = _stopwatchState as Flow<StopwatchState>


    override fun onCreate() {
        with((application as ClockyApplication).getServiceContainer()) {
            stopwatch = provideStopwatch()
            serviceScope = provideCoroutineScope()
            millisInTimeStringFormatter = provideMillisFormatter()
            millisToSecondsFormatter = provideMillisInSecondsFormatter()
            formattedElapsedMillisStream = stopwatch
                .millisElapsedStream
                .map(millisInTimeStringFormatter::formatMillis)
            notificationBuilder = provideStopwatchNotificationBuilder(this@StopwatchService)
        }
    }

    /**
     * Starts the stopwatch and sets its state to [StopwatchState.RUNNING].
     */
    fun startStopwatch() {
        _stopwatchState.value = StopwatchState.RUNNING
        serviceScope.launch {
            stopwatch.start()
        }
        stopwatch.millisElapsedStream
            .map(millisToSecondsFormatter::formatMillis)
            .distinctUntilChanged()
            .onEach(::createOrUpdateNotification)
            .launchIn(serviceScope)
    }

    private fun createOrUpdateNotification(timeText: String) {
        // fixme
        notificationManager.notify(
            FOREGROUND_NOTIFICATION_ID,
            notificationBuilder.buildNotification(timeText)
        )
    }

    /**
     * Pauses the stopwatch and sets its state to [StopwatchState.PAUSED].
     */
    fun pauseStopwatch() {
        stopwatch.pause()
        _stopwatchState.value = StopwatchState.PAUSED
    }

    /**
     * Stops and resets the stopwatch. It also sets its state to [StopwatchState.RESET].
     */
    fun stopAndResetStopwatch() {
        stopwatch.reset()
        _stopwatchState.value = StopwatchState.RESET
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            FOREGROUND_NOTIFICATION_ID,
            notificationBuilder.buildNotification("00:00")
        )
        return START_NOT_STICKY // todo check
    }

    override fun onBind(intent: Intent): IBinder = stopwatchServiceBinder
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
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

    companion object {
        private const val FOREGROUND_NOTIFICATION_ID = -20
    }
}
