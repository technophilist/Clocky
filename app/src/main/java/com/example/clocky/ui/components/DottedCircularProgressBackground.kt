package com.example.clocky.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin


/**
 * This class holds the state of the [DottedCircularProgressBackground].
 *
 * @property isReset A property that indicates whether the progress has been reset.
 * @property isRunning A property that indicates whether the animation is currently running.
 */
class DottedCircularProgressBackgroundState(isInitiallyRunning: Boolean) {
    var isReset by mutableStateOf(true)
        private set

    var isRunning by mutableStateOf(isInitiallyRunning)
        private set

    init {
        // fixme - though the animation starts, if this class is recreated after a config change
        //  the animation will always start from 90 degrees, which is not the desired
        //  result.
        if (isInitiallyRunning) start()
    }

    /**
     * Used to start the animation.
     */
    fun start() {
        isReset = false
        isRunning = true
    }

    /**
     * Used to pause the progress.
     */
    fun pause() {
        isRunning = false
    }

    /**
     * Used to stop and reset the animation.
     */
    fun stopAndReset() {
        isReset = true
        isRunning = false
    }

    companion object {
        /**
         * A saver object that can be used to save and restore the state of a
         * [DottedCircularProgressBackgroundState] instance.
         */
        val Saver = listSaver(
            save = { listOf(it.isRunning) },
            restore = { DottedCircularProgressBackgroundState(it[0]) }
        )
    }
}

/**
 * Used to create and remember a [DottedCircularProgressBackgroundState] instance with the
 * given initial running state set to [isInitiallyRunning]. The [isInitiallyRunning] set to `false',
 * by default.
 */
@Composable
fun rememberDottedCircularProgressBackgroundState(
    isInitiallyRunning: Boolean = false
): DottedCircularProgressBackgroundState = rememberSaveable(
    saver = DottedCircularProgressBackgroundState.Saver,
    init = { DottedCircularProgressBackgroundState(isInitiallyRunning = isInitiallyRunning) }
)

/**
 * todo - need to explain how the math behind this composable works
 */
@Composable
fun DottedCircularProgressBackground(
    state: DottedCircularProgressBackgroundState,
    dotColor: Color = MaterialTheme.colors.primary,
    content: @Composable () -> Unit
) {

    val coordinates = remember { mutableStateListOf<Pair<Float, Float>>() }
    val initialDegree = remember { -90 }
    var currentDegree by rememberSaveable { mutableStateOf(initialDegree) }
    LaunchedEffect(state.isRunning, state.isReset) {
        if (state.isReset) {
            currentDegree = initialDegree
            coordinates.clear()
            return@LaunchedEffect
        }
        while (state.isRunning && isActive) {
            for (degree in currentDegree..270 step 6) {
                delay(25)
                val degreeInRadians = Math.toRadians(degree.toDouble()).toFloat()
                val x = cos(degreeInRadians)
                val y = sin(degreeInRadians)
                coordinates.add(Pair(x, y))
                currentDegree = degree
            }
            currentDegree = initialDegree
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!state.isReset) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.width / 2
                // draw trail of the leading dot
                for (i in coordinates.indices) {
                    for (j in (i - 6) until i) {
                        if (j !in coordinates.indices) break
                        val alpha = when (j) {
                            i - 1 -> 1f // alpha to be used for the first dot behind the leading dot
                            i - 2 -> 0.8f // alpha to be used for the second dot behind the leading dot
                            i - 3 -> 0.6f // alpha to be used for the third dot behind the leading dot
                            i - 4 -> 0.4f // alpha to be used for the fourth dot behind the leading dot
                            i - 5 -> 0.2f // alpha to be used for the fifth dot behind the leading dot
                            else -> 0f // alpha to be used for the sixth dot behind the leading dot
                        }
                        drawCircle(
                            color = dotColor,
                            center = Offset(
                                x = center.x + (radius * coordinates[j].first),
                                y = center.y + (radius * coordinates[j].second)
                            ),
                            radius = 4f,
                            colorFilter = ColorFilter.tint(
                                color = Color.White.copy(alpha = alpha),
                                blendMode = BlendMode.SrcOver
                            )
                        )
                    }
                    // the leading dot
                    drawCircle(
                        color = Color.White,
                        center = Offset(
                            x = center.x + (radius * coordinates[i].first),
                            y = center.y + (radius * coordinates[i].second)
                        ),
                        radius = 4f
                    )
                }
            }
        }
        Box(
            modifier = Modifier.align(Alignment.Center),
            content = { content() }
        )
    }
}
