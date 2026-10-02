package com.mel182.callbackflowexample.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationTrackerViewState(val requiredPermissionGranted: Boolean = false, val isTracking: Boolean = false, val locationData: LocationData? = null): Parcelable

@Parcelize
data class LocationData(val lat: Double = 0.0, val lng: Double = 0.0, val altitude: Double = 0.0): Parcelable
