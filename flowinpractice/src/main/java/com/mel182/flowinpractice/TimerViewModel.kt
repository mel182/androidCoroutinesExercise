package com.mel182.flowinpractice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
import java.util.Locale

class TimerViewModel : ViewModel() {

    val timeEmitFlow = timerAndEmit(100f)
    val formattedTime = timeEmitFlow
        .runningReduce { totalElapsedTime, newElapsedTime ->
            totalElapsedTime + newElapsedTime
        }.map { totalElapsedTime ->
            totalElapsedTime.toComponents { hours, minutes, seconds, nanoseconds ->
                String.format(
                    Locale.forLanguageTag("nl-NL"),
                    "%02d:%02d:%02d:%02d",
                    hours,
                    minutes,
                    seconds,
                    nanoseconds / (1_000_000L * 10L)
                )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            "00:00:00:00"
        )

    val totalProgressTimeMillis = 10000L
    // Such progress example can also be used to display an upload progress
    val progress = timeEmitFlow
        .runningReduce { totalElapsedTime, newElapsedTime ->
            totalElapsedTime + newElapsedTime
        }.map { totalDuration ->
            (totalDuration.inWholeMilliseconds / totalProgressTimeMillis.toFloat()).coerceIn(0f, 1f)
        }.filter { progressFraction ->
            progressFraction in (0f..1f)
        } .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            0f
        )
}