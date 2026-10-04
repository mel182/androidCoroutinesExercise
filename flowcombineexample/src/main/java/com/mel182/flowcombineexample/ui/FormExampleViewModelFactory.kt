@file:Suppress("UNCHECKED_CAST")

package com.mel182.flowcombineexample.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

class FormExampleViewModelFactory: ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

        if (modelClass.isAssignableFrom(FormExampleViewModel::class.java)) {
            return FormExampleViewModel() as T
        }

        throw Exception("Not assigned from ${FormExampleViewModel::class.java.simpleName}")
    }
}