package com.mel182.flowzipexample.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationRouteViewState(val requiredPermissionGranted: Boolean = false, val isTracking: Boolean = false, val routeData: SpeedsData? = null): Parcelable

@Parcelize
data class SpeedsData(val distance: Float = 0f, val averageSpeed: Double = 0.0): Parcelable
