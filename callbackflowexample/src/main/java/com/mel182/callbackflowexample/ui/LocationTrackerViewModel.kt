package com.mel182.callbackflowexample.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mel182.callbackflowexample.data.LocationData
import com.mel182.callbackflowexample.data.LocationTrackerViewState
import com.mel182.callbackflowexample.data.LocationTrackerAction
import com.mel182.callbackflowexample.domain.LocationObserver
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.seconds

class LocationTrackerViewModel(private val locationObserver: LocationObserver) : ViewModel() {

    private val _viewState = MutableStateFlow(LocationTrackerViewState())
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
        trackingJob = locationObserver.observeLocation(interval = 2.seconds).onEach { location ->
            _viewState.update { it.copy(locationData = LocationData(
                lat = location.latitude,
                lng = location.longitude,
                altitude = location.altitude
            )
            ) }
        }.launchIn(viewModelScope)
    }

    private fun stopTracking() {
        _viewState.update { it.copy(isTracking = false) }
        trackingJob?.cancel()
        trackingJob = null
    }
}