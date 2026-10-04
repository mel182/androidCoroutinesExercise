package com.mel182.flowzipexample.ui

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mel182.flowzipexample.data.LocationRouteViewState
import com.mel182.flowzipexample.data.LocationTrackerAction
import com.mel182.flowzipexample.data.SpeedsData
import com.mel182.flowzipexample.domain.LocationObserver
import com.mel182.flowzipexample.domain.timerAndEmit
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class LocationTrackerViewModel(private val locationObserver: LocationObserver) : ViewModel() {

    private val _viewState = MutableStateFlow(LocationRouteViewState())
    val viewState = _viewState.asStateFlow()

    private var trackingJob: Job? = null

    fun performAction(action: LocationTrackerAction) {
        when (action) {

            is LocationTrackerAction.RequiredLocationPermissionGranted -> {
                _viewState.update { it.copy(requiredPermissionGranted = action.granted) }
            }

            is LocationTrackerAction.StartTracking -> startTracking()

            is LocationTrackerAction.StopTracking -> stopTracking()
        }
    }

    private fun startTracking() {
        _viewState.update { it.copy(isTracking = true) }

        trackingJob = timerAndEmit(3f)
            .runningReduce { totalElapsedTime, newElapsedTime ->
                totalElapsedTime + newElapsedTime
            }
            .zip(locationObserver.observeLocation(interval = 1.seconds)) { totalDuration, location ->
                totalDuration to location
            }.onEach { (totalDuration, location) ->
                Log.i(
                    "TAG35",
                    "Location (${location.latitude}, ${location.longitude}) was tracked after ${totalDuration.inWholeMilliseconds} milliseconds"
                )
            }
            .runningFold(initial = emptyList<Pair<Duration, Location>>()) { locations, newLocation ->
                locations + newLocation
            }.map { allLocations ->
                var distanceMade = 0f
                val averageSpeed =
                    allLocations.zipWithNext { (duration1, location1), (duration2, location2) ->
                        val distance = location1.distanceTo(location2)
                        val durationDifference =
                            (duration2 - duration1).toDouble(DurationUnit.HOURS)
                        distanceMade = distance

                        if (durationDifference > 0.0) {
                            ((distance / 1000.0) / durationDifference)
                        } else 0.0
                    }.average()

                //BigDecimal(avg).setScale(2, RoundingMode.HALF_UP).toDouble()

                Log.i("TAG37", " avg speed: ${averageSpeed} - distance: ${distanceMade}")
                Pair(
                    if (averageSpeed.isNaN()) 0.0 else BigDecimal(averageSpeed).setScale(2, RoundingMode.HALF_UP).toDouble(),
                    if (distanceMade.isNaN()) 0f else distanceMade.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toFloat()
                )
            }.onEach { (speed, distance) ->

                updateSpeedData(distance = distance, averageSpeed = speed)

                Log.i(
                    "TAG36",
                    "Average speed is $speed km/h"
                )
                //println("Average speed is $avgSpeed km/h")
            }.launchIn(viewModelScope)
    }

    private fun updateSpeedData(distance: Float, averageSpeed: Double) {
        _viewState.update {
            val currentData = SpeedsData(
                distance = distance,
                averageSpeed = if (averageSpeed.isNaN()) 0.0 else averageSpeed
            )
            Log.i("TAG37", "")
            Log.i("TAG37", "Current data: $currentData")
            Log.i("TAG37", "Current state: ${it.routeData}")
            Log.i("TAG37", "")
            it.copy(routeData = currentData)
        }
    }

    private fun stopTracking() {
        _viewState.update { it.copy(isTracking = false) }
        trackingJob?.cancel()
        trackingJob = null
    }
}