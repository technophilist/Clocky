package com.example.clocky.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.math.cos
import kotlin.math.sin


/**
 * This class holds the state of the [DottedCircularProgressBackground].
 */
class DottedCircularProgressBackgroundState(
    isInitiallyRunning: Boolean,
    initialDegree: Int
) {
    /**
     * A [State] that indicates whether the animation is in the "Reset" state.
     */
    var isReset by mutableStateOf(true)
        private set

    /**
     * A [State] that indicates whether the animation is currently running.
     */
    var isRunning by mutableStateOf(isInitiallyRunning)
        private set

    /**
     * A [State] that indicates that number of iterations that have occurred around the
     * circumference of the circle.
     */
    var numberOfIterationsAroundCircle by mutableStateOf(0)
        private set

    /**
     * A property that holds the previously emitted degree value. Useful to restore
     * the state of the animation when it is resumed after getting paused.
     */
    var previouslyEmittedDegree: Int = initialDegree
        private set

    /**
     * A [Flow] that emits the current degree, from the center of the drawing area, where
     * a dot is about to be drawn.
     *
     * Note: The [transformLatest] operator has to be used here because of the following reason.
     * If the transform block of [combine] operator gets blocked by an infinite loop (in this case,
     * an infinite loop of degrees), then any updates to the flows that are passed as a parameter
     * of the [combine] block will not trigger the [combine] operator's transform block, because the
     * transform block is already performing an operation that never stops (ie. a blocking operation).
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    var currentDegree = combine(
        snapshotFlow { isReset },
        snapshotFlow { isRunning }
    ) { isReset, isRunning -> Pair(isReset, isRunning) }
        .transformLatest { (isAnimationReset, isAnimationRunning) ->
            if (isAnimationReset) {
                emit(INITIAL_DEGREE)
                resetPreviouslyEmittedDegree()
                return@transformLatest
            }
            if (!isAnimationRunning) return@transformLatest
            while (isAnimationRunning) {
                for (i in (previouslyEmittedDegree..270) step 6) {
                    delay(30)
                    emit(i)
                    previouslyEmittedDegree = i
                }
                resetPreviouslyEmittedDegree()
                numberOfIterationsAroundCircle++
            }
        }


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
        numberOfIterationsAroundCircle = 0
        resetPreviouslyEmittedDegree()
    }

    /**
     * Used to reset the previously emitted degree to [INITIAL_DEGREE].
     */
    private fun resetPreviouslyEmittedDegree() {
        previouslyEmittedDegree = INITIAL_DEGREE
    }

    companion object {
        /**
         * A saver object that can be used to save and restore the state of a
         * [DottedCircularProgressBackgroundState] instance.
         */
        val Saver = listSaver(
            save = {
                listOf(if (it.isRunning) 1 else 0, it.previouslyEmittedDegree)
            },
            restore = {
                DottedCircularProgressBackgroundState(
                    isInitiallyRunning = it[0] == 1,
                    initialDegree = it[1]
                )
            }
        )

        const val INITIAL_DEGREE = -90
    }
}

/**
 * Used to create and remember a [DottedCircularProgressBackgroundState] instance with the
 * given initial running state set to [isInitiallyRunning]. The [isInitiallyRunning] set to `false',
 * by default.
 */
@Composable
fun rememberDottedCircularProgressBackgroundState(
    isInitiallyRunning: Boolean = false,
    startDegree: Int = -90
): DottedCircularProgressBackgroundState = rememberSaveable(
    saver = DottedCircularProgressBackgroundState.Saver,
    init = {
        DottedCircularProgressBackgroundState(
            isInitiallyRunning = isInitiallyRunning,
            initialDegree = startDegree
        )
    }
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
    val stepValue = remember { 6 }
    val currentDegree by state.currentDegree.collectAsState(initial = -90)
    val isPrefilledCanvasVisible by remember {
        derivedStateOf { state.numberOfIterationsAroundCircle > 0 }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isPrefilledCanvasVisible) {
            CanvasWithDottedCircle(
                modifier = Modifier.fillMaxSize(),
                radius = { size.width / 2 },
                stepValue = stepValue
            )
        }
        if (!state.isReset) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.width / 2
                for (degree in -90..currentDegree step stepValue) {
                    // apply color filters from the 6th dot from the leading dot, to the leading dot
                    val colorFilterAlpha = when (degree) {
                        (currentDegree - (stepValue * 6)) -> 0.0f
                        (currentDegree - (stepValue * 5)) -> 0.2f
                        (currentDegree - (stepValue * 4)) -> 0.4f
                        (currentDegree - (stepValue * 3)) -> 0.6f
                        (currentDegree - (stepValue * 2)) -> 0.8f
                        (currentDegree - (stepValue * 1)) -> 1f
                        currentDegree -> 1f
                        else -> 0f
                    }
                    drawCircle(
                        color = dotColor,
                        center = Offset(
                            x = center.x + (radius * cos(degree)),
                            y = center.y + (radius * sin(degree))
                        ),
                        radius = 4f,
                        colorFilter = ColorFilter.tint(
                            color = Color.White.copy(alpha = colorFilterAlpha),
                            blendMode = BlendMode.SrcOver
                        )
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

/**
 * A [Canvas] with it's circumference filled with a Dotted Circle pattern.
 * // todo update docs
 */
@Composable
private fun CanvasWithDottedCircle(
    radius: DrawScope.() -> Float,
    stepValue: Int,
    modifier: Modifier = Modifier,
    dotColor: Color = MaterialTheme.colors.primary
) {
    Canvas(modifier = modifier) {
        for (degree in -90..270 step stepValue) {
            drawCircle(
                color = dotColor,
                center = Offset(
                    x = center.x + (radius() * cos(degree)),
                    y = center.y + (radius() * sin(degree))
                ),
                radius = 4f
            )
        }
    }
}

/**
 * A utility function that returns the sine value (in [Float]) of a [degree] represented
 * as an [Int].
 */
private fun sin(degree: Int): Float {
    val degreesInRadians = Math.toRadians(degree.toDouble())
    return sin(degreesInRadians).toFloat()
}

/**
 * A utility function that returns the cosine value (in [Float]) of a [degree] represented
 * as an [Int].
 */
private fun cos(degree: Int): Float {
    val degreesInRadians = Math.toRadians(degree.toDouble())
    return cos(degreesInRadians).toFloat()
}