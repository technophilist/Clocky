package com.example.clocky.di

import com.example.clocky.domain.stopwatch.ClockyStopwatch
import com.example.clocky.domain.stopwatch.Stopwatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import android.app.Service
import android.content.Context
import com.example.clocky.domain.millisformatter.MillisInFullTimeStringFormatter
import com.example.clocky.domain.millisformatter.MillisFormatter
import com.example.clocky.domain.millisformatter.MillisInSecondsFormatter
import com.example.clocky.stopwatchservice.notification.ClockyStopwatchNotificationBuilder
import com.example.clocky.stopwatchservice.notification.StopwatchNotificationBuilder

/**
 * A DI container for dependencies that are to be injected in a [Service].
 */
class ServiceContainer {
    /**
     * Provides an instance of [CoroutineScope].
     */
    fun provideCoroutineScope() = CoroutineScope(Dispatchers.Default)

    /**
     * Provides a concrete implementation of a [Stopwatch].
     */
    fun provideStopwatch(): Stopwatch = ClockyStopwatch()

    /**
     * Provides a concrete implementation of [MillisFormatter].
     */
    @Deprecated(
        message = "Use provideDefaultMillisFormatter()",
        replaceWith = ReplaceWith("provideDefaultMillisFormatter()")
    )
    fun provideMillisFormatter(): MillisFormatter = MillisInFullTimeStringFormatter()

    /**
     * Provides an instance of [MillisInFullTimeStringFormatter].
     */
    fun provideDefaultMillisFormatter(): MillisFormatter = MillisInFullTimeStringFormatter()

    /**
     * Provides an instance of [MillisInSecondsFormatter].
     */
    fun provideMillisInSecondsFormatter() = MillisInSecondsFormatter()

    /**
     * Provides a concrete implementation of [StopwatchNotificationBuilder].
     */
    fun provideStopwatchNotificationBuilder(context: Context): StopwatchNotificationBuilder {
        return ClockyStopwatchNotificationBuilder(context)
    }
}