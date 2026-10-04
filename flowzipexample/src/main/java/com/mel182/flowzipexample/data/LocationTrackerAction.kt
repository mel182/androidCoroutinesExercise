package com.mel182.flowzipexample.data

sealed interface LocationTrackerAction {
    data class RequiredLocationPermissionGranted(val granted: Boolean): LocationTrackerAction
    data object StartTracking: LocationTrackerAction
    data object StopTracking: LocationTrackerAction
}