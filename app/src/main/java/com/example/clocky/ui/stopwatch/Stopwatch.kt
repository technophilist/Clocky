package com.example.clocky.ui.stopwatch

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.*

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
        fontWeight = FontWeight.SemiBold,
        color = Color.White
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
    val contentSizeAnimationDurationMillis = 100
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .animateContentSize(animationSpec = tween(contentSizeAnimationDurationMillis)),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                if (isStopwatchRunning) onPauseButtonClick()
                else onPlayButtonClick()
            },
            content = { Icon(imageVector = playPauseIcon, contentDescription = null) }
        )
        AnimatedVisibility(
            visible = isStopButtonEnabled,
            enter = fadeIn(animationSpec = tween(delayMillis = contentSizeAnimationDurationMillis)),
            exit = fadeOut(animationSpec = tween(durationMillis = contentSizeAnimationDurationMillis))
        ) {
            Button(
                onClick = onStopButtonClick,
                content = { Icon(imageVector = Icons.Filled.Stop, contentDescription = null) }
            )
        }
    }
}