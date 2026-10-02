package com.mel182.flowinpractice

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun timerAndEmit(emissionPerSecond: Float): Flow<Duration> {
    return flow {
        var lastEmitTime = System.currentTimeMillis()
        emit(Duration.ZERO)

        while(true) {
            delay((1000L / emissionPerSecond).roundToLong().milliseconds)

            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - lastEmitTime

            emit(elapsedTime.milliseconds)
            lastEmitTime = currentTime
        }


    }
}