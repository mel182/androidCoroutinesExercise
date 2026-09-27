@file:Suppress("UNCHECKED_CAST")

package com.mel182.coroutinesynchronizationexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.CoroutineScope

class LeaderBoardViewModelFactory(private val repo: LeaderBoardRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

        if (modelClass.isAssignableFrom(LeaderBoardViewModel::class.java)) {
            return LeaderBoardViewModel(repo = repo) as T
        }

        throw IllegalArgumentException("Not ${LeaderBoardViewModel::class.simpleName} class")
    }
}