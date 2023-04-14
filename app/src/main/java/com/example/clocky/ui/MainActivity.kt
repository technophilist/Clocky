package com.example.clocky.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clocky.domain.stopwatch.ClockyStopwatch
import com.example.clocky.ui.stopwatch.StopWatchViewModel
import com.example.clocky.ui.stopwatch.Stopwatch
import com.example.clocky.ui.stopwatch.StopwatchViewModelFactory
import com.example.clocky.ui.theme.ClockyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockyTheme {
                val viewModelFactory = remember {
                    StopwatchViewModelFactory(stopwatch = ClockyStopwatch())
                }
                val viewModel = viewModel<StopWatchViewModel>(factory = viewModelFactory)
                Stopwatch(stopWatchViewModel = viewModel)
            }
        }
    }
}