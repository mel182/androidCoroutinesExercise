package com.mel182.callbackflowexample.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.mel182.callbackflowexample.domain.LocationObserver

class LocationTrackerViewModelFactory(private val locationObserver: LocationObserver): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

        if (modelClass.isAssignableFrom(LocationTrackerViewModel::class.java)) {
            return LocationTrackerViewModel(locationObserver = locationObserver) as T
        }

        throw Exception("Not assigned from ${LocationTrackerViewModel::class.java.simpleName}")
    }
}