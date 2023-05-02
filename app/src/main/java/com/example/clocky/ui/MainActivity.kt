package com.example.clocky.ui

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.clocky.ui.components.DottedCircularProgressBackground
import com.example.clocky.ui.components.DottedCircularProgressBackgroundState
import com.example.clocky.ui.components.rememberDottedCircularProgressBackgroundState
import com.example.clocky.ui.stopwatch.Stopwatch
import com.example.clocky.ui.stopwatch.StopwatchService
import com.example.clocky.ui.theme.ClockyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var serviceConnection: ServiceConnection? = null
    private var stopwatchService: StopwatchService? = null
    private val elapsedTimeStringStream = MutableStateFlow("00:00:00:00")
    private val stopwatchStateStream = MutableStateFlow<StopwatchService.StopwatchState?>(null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isStopwatchServiceRunning()) startStopwatchService()
        bindStopwatchService()
        setContent { ClockyTheme(content = { ClockyApp() }) }
    }

    @Composable
    private fun ClockyApp() {
        val elapsedTimeString by elapsedTimeStringStream.collectAsStateWithLifecycle()
        val stopwatchState by stopwatchStateStream.collectAsStateWithLifecycle()
        val isInitiallyRunning = remember{
            stopwatchState == StopwatchService.StopwatchState.RUNNING
        }
        val dottedProgressBackgroundState = rememberDottedCircularProgressBackgroundState(
            isInitiallyRunning = isInitiallyRunning
        )
        val isStopButtonEnabled = remember(stopwatchState) {
            stopwatchState != null && stopwatchState != StopwatchService.StopwatchState.RESET
        }
        val isStopwatchRunning = remember(stopwatchState) {
            stopwatchState == StopwatchService.StopwatchState.RUNNING
        }
        DottedCircularProgressBackground(state = dottedProgressBackgroundState) {
            Stopwatch(
                elapsedTimeText = { elapsedTimeString },
                onPlayButtonClick = {
                    stopwatchService?.startStopwatch()
                    dottedProgressBackgroundState.start()
                },
                onPauseButtonClick = {
                    stopwatchService?.pauseStopwatch()
                    dottedProgressBackgroundState.pause()
                },
                onStopButtonClick = {
                    stopwatchService?.stopAndResetStopwatch()
                    dottedProgressBackgroundState.stopAndReset()
                },
                isStopButtonEnabled = isStopButtonEnabled,
                isStopwatchRunning = isStopwatchRunning
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceConnection?.let(::unbindService)
        if (
            isStopwatchServiceRunning() &&
            stopwatchStateStream.value == StopwatchService.StopwatchState.RESET
        ) stopStopwatchService()
    }

    /**
     * Checks if the [StopwatchService] is running.
     *
     * @return true if the StopwatchService is running, false otherwise.
     */
    @Suppress("DEPRECATION")
    private fun isStopwatchServiceRunning(): Boolean {
        return (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Int.MAX_VALUE)
            .any { it.service.className == StopwatchService::class.java.name }
    }

    /**
     * Used to start the [StopwatchService].
     */
    private fun startStopwatchService() {
        val intent = Intent(this, StopwatchService::class.java)
        startService(intent)
    }

    /**
     * Used to stop the [StopwatchService].
     */
    private fun stopStopwatchService() {
        val intent = Intent(this, StopwatchService::class.java)
        stopService(intent)
    }

    /**
     * Used to bind the activity to the [StopwatchService].
     */
    private fun bindStopwatchService() {
        val intent = Intent(this, StopwatchService::class.java)
        serviceConnection = createServiceConnection()
        bindService(
            intent,
            serviceConnection!!,
            Context.BIND_IMPORTANT
        )
    }

    /**
     * Creates a [ServiceConnection] to connect to the [StopwatchService].
     */
    private fun createServiceConnection() = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            val stopwatchServiceBinder = binder as StopwatchService.StopwatchServiceBinder
            stopwatchService = stopwatchServiceBinder.service
            repeatOnLifecycleInLifecycleScope {
                stopwatchService!!
                    .formattedElapsedMillisStream
                    .collect { elapsedTimeStringStream.value = it }
            }
            repeatOnLifecycleInLifecycleScope {
                stopwatchService!!
                    .stopwatchState
                    .collect { stopwatchStateStream.value = it }
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            serviceConnection = null
        }
    }

    /**
     * A utility function that is a shortcut for the following code snippet.
     * ```
     * lifecycleScope.launch {
     *       repeatOnLifecycle(...){...}
     * }
     * ```
     */
    private fun ComponentActivity.repeatOnLifecycleInLifecycleScope(
        state: Lifecycle.State = Lifecycle.State.STARTED,
        block: suspend CoroutineScope.() -> Unit
    ): Job = lifecycleScope.launch { repeatOnLifecycle(state, block) }
}
