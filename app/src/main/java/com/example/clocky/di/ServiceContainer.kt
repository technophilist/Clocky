package com.example.clocky.di

import com.example.clocky.domain.stopwatch.ClockyStopwatch
import com.example.clocky.domain.stopwatch.Stopwatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import android.app.Service
import com.example.clocky.domain.millisformatter.ClockyMillisFormatter
import com.example.clocky.domain.millisformatter.MillisFormatter

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
    fun provideMillisFormatter(): MillisFormatter = ClockyMillisFormatter()
}