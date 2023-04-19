package com.example.clocky.ui

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clocky.domain.stopwatch.Stopwatch
import com.example.clocky.ui.stopwatch.StopWatchViewModel
import com.example.clocky.ui.stopwatch.Stopwatch
import com.example.clocky.ui.stopwatch.StopwatchService
import com.example.clocky.ui.stopwatch.StopwatchViewModelFactory
import com.example.clocky.ui.theme.ClockyTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.logging.LogManager

class MainActivity : ComponentActivity() {
    private var stopwatch by mutableStateOf<Stopwatch?>(null)
    private var serviceConnection: ServiceConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ClockyTheme {
                stopwatch?.let {
                    val stopwatchViewModelFactory =
                        remember { StopwatchViewModelFactory(stopwatch = it) }
                    val stopWatchViewModel =
                        viewModel<StopWatchViewModel>(factory = stopwatchViewModelFactory)
                    val elapsedTimeString by stopWatchViewModel.currentMillisTextStream
                        .collectAsStateWithLifecycle(initialValue = "00:00:00:00")
                    val uiState by stopWatchViewModel.uiState.collectAsStateWithLifecycle()
                    Stopwatch(
                        elapsedTimeText = { elapsedTimeString },
                        onPlayButtonClick = stopWatchViewModel::start,
                        onPauseButtonClick = stopWatchViewModel::pause,
                        onStopButtonClick = {
                            stopWatchViewModel.stopAndReset()
                            stopStopwatchService()
                        },
                        isStopButtonEnabled = uiState != StopWatchViewModel.UiState.RESET,
                        isStopwatchRunning = uiState == StopWatchViewModel.UiState.RUNNING
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (isStopwatchServiceRunning()) {
            bindStopwatchService()
        } else {
            startStopwatchService()
            bindStopwatchService()
        }
    }

    override fun onStop() {
        super.onStop()
        serviceConnection?.let(::unbindService)
    }
    /**
     * Checks if the [StopwatchService] is running.
     *
     * @return true if the StopwatchService is running, false otherwise.
     */
    @Suppress("DEPRECATION")
    private fun isStopwatchServiceRunning(): Boolean {
        return (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(4)
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
            stopwatch = stopwatchServiceBinder.stopwatch
        }
        override fun onServiceDisconnected(componentName: ComponentName) {}
    }
}
