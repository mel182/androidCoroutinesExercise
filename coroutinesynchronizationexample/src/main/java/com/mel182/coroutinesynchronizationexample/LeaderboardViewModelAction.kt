package com.mel182.coroutinesynchronizationexample

sealed interface LeaderboardViewModelAction {
    data class ToggleSimulation(val simulate: Boolean): LeaderboardViewModelAction
}