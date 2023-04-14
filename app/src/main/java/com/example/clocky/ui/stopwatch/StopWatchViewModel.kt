package com.example.clocky.ui.stopwatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.clocky.domain.stopwatch.Stopwatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


/**
 * A ViewModel that provides a stopwatch functionality.
 * @param stopwatch The concrete implementation of [Stopwatch] to use.
 */
class StopWatchViewModel(private val stopwatch: Stopwatch) : ViewModel() {

    /**
     * A stream that contains the current UI state of the stopwatch.
     */
    private val _uiState = MutableStateFlow(UiState.RESET)
    val uiState = _uiState.asStateFlow()

    /**
     * The date time formatter used to format the elapsed time.
     */
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS")

    /**
     * A stream of the elapsed time in milliseconds, formatted as a string.
     */
    val currentMillisTextStream = stopwatch
        .millisElapsedStream
        .map {
            LocalDateTime.ofInstant(
                Instant
                    .ofEpochMilli(0)
                    .plusMillis(it),
                ZoneId.of("Etc/GMT")
            ).toLocalTime().format(dateTimeFormatter)
        }

    /**
     * Use to start the stopwatch.
     */
    fun start() {
        _uiState.value = UiState.RUNNING
        viewModelScope.launch {
            stopwatch.start()
        }
    }

    /**
     * Use to pause the stopwatch.
     */
    fun pause() {
        stopwatch.pause()
        _uiState.value = UiState.PAUSED
    }

    /**
     * Used to stop and reset the stopwatch.
     */
    fun stopAndReset() {
        stopwatch.reset()
        _uiState.value = UiState.RESET
    }

    /**
     * An enum representing the different UI states.
     */
    enum class UiState { RESET, PAUSED, RUNNING }
}

@Suppress("UNCHECKED_CAST")
class StopwatchViewModelFactory(private val stopwatch: Stopwatch) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>) = StopWatchViewModel(stopwatch) as T
}