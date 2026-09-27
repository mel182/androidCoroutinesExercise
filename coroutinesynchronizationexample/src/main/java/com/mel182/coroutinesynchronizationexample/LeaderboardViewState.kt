package com.mel182.coroutinesynchronizationexample

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

private val playerList = listOf(
    Player(id = 1,name = "Player 1", score = 0),
    Player(id = 2,name = "Player 2", score = 0),
    Player(id = 3,name = "Player 3", score = 0),
    Player(id = 4,name = "Player 4", score = 0),
    Player(id = 5,name = "Player 5", score = 0),
    Player(id = 6,name = "Player 6", score = 0),
    Player(id = 7,name = "Player 7", score = 0),
    Player(id = 8,name = "Player 8", score = 0),
    Player(id = 9,name = "Player 9", score = 0),
)

@Parcelize
data class LeaderboardViewState(
    val isSimulating: Boolean = false,
    val scoreList: List<Player> = playerList) : Parcelable
