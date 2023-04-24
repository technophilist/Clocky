package com.example.clocky.ui.stopwatch

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.*

/**
 * A stateful implementation of Stopwatch that makes use of the [StopWatchViewModel].
 */
@Composable
fun Stopwatch(stopWatchViewModel: StopWatchViewModel) {
    val elapsedTimeString by stopWatchViewModel.currentMillisTextStream
        .collectAsStateWithLifecycle(initialValue = "00:00:00:000")
    val uiState by stopWatchViewModel.uiState.collectAsStateWithLifecycle()
    Stopwatch(
        elapsedTimeText = { elapsedTimeString },
        onPlayButtonClick = stopWatchViewModel::start,
        onPauseButtonClick = stopWatchViewModel::pause,
        onStopButtonClick = stopWatchViewModel::stopAndReset,
        isStopButtonEnabled = uiState != StopWatchViewModel.UiState.RESET,
        isStopwatchRunning = uiState == StopWatchViewModel.UiState.RUNNING
    )
}

/**
 * A stopwatch composable.
 *
 * @param elapsedTimeText A lambda that returns the current elapsed time as a string.
 * @param onPlayButtonClick callback that will be executed when the play button is clicked.
 * @param onPauseButtonClick callback that will be executed when the pause button is clicked.
 * @param onStopButtonClick callback that will be executed when the stop button is clicked.
 * @param isStopButtonEnabled indicates whether or not the stop button is enabled.
 * @param isStopwatchRunning indicates whether or not the stopwatch is currently running.
 */
@Composable
fun Stopwatch(
    elapsedTimeText: () -> String,
    onPlayButtonClick: () -> Unit,
    onPauseButtonClick: () -> Unit,
    onStopButtonClick: () -> Unit,
    isStopButtonEnabled: Boolean,
    isStopwatchRunning: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElapsedTimeText(timeText = elapsedTimeText)
        Spacer(modifier = Modifier.size(16.dp))
        ButtonRow(
            onPlayButtonClick = onPlayButtonClick,
            onPauseButtonClick = onPauseButtonClick,
            onStopButtonClick = onStopButtonClick,
            isStopButtonEnabled = isStopButtonEnabled,
            isStopwatchRunning = isStopwatchRunning
        )
    }
}

/**
 * This is a wrapper over the [Text] composable. Since the text might keep changing as frequently
 * as every millisecond, the wrapper acts as a recomposition scope, preventing the need to
 * re-compose the entire composable that needs to display the elapsed time.
 */
@Composable
private fun ElapsedTimeText(timeText: () -> String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = timeText(),
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun ButtonRow(
    onPlayButtonClick: () -> Unit,
    onPauseButtonClick: () -> Unit,
    onStopButtonClick: () -> Unit,
    isStopButtonEnabled: Boolean,
    isStopwatchRunning: Boolean,
) {
    val playPauseIcon = if (isStopwatchRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = {
                if (isStopwatchRunning) onPauseButtonClick()
                else onPlayButtonClick()
            },
            content = { Icon(imageVector = playPauseIcon, contentDescription = null) }
        )
        Button(
            enabled = isStopButtonEnabled,
            onClick = onStopButtonClick,
            content = { Icon(imageVector = Icons.Filled.Stop, contentDescription = null) }
        )
    }
}