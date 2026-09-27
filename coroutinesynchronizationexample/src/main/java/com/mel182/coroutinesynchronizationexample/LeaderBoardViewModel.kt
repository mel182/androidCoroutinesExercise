package com.mel182.coroutinesynchronizationexample

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeaderBoardViewModel(private val repo: LeaderBoardRepository): ViewModel() {

    private val _leaderboardViewList: MutableStateFlow<LeaderboardViewState> = MutableStateFlow(LeaderboardViewState())
    val leaderboardViewState: StateFlow<LeaderboardViewState>
        get() = _leaderboardViewList.asStateFlow()


    fun performAction(action: LeaderboardViewModelAction) {
        when(action) {
            is LeaderboardViewModelAction.ToggleSimulation -> toggleSimulation(action.simulate)
        }
    }

    private fun toggleSimulation(start: Boolean) {

        if (start) {
         _leaderboardViewList.update { it.copy(isSimulating = true) }

            repo.start(scope2 = viewModelScope)
            viewModelScope.launch {
                repo.score.collectLatest { playerRanking ->

                    if (playerRanking.isNotEmpty()) {
                        Log.i("TAG35","Player ranking -> ${playerRanking}")
                        _leaderboardViewList.update { it.copy(scoreList = playerRanking) }
                    }
                }
            }

        } else {
            _leaderboardViewList.update { it.copy(isSimulating = false) }
            repo.stop()
        }
    }
}