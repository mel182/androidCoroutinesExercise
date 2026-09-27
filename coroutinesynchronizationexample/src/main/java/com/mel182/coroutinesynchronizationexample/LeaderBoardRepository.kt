package com.mel182.coroutinesynchronizationexample

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class LeaderBoardRepository {

    private val scores = hashMapOf<Int, Player>(
        1 to Player(id = 1, name = "Player 1", score = 0),
        2 to Player(id = 2, name = "Player 2", score = 0),
        3 to Player(id = 3, name = "Player 3", score = 0),
        4 to Player(id = 4, name = "Player 4", score = 0),
        5 to Player(id = 5, name = "Player 5", score = 0),
        6 to Player(id = 6, name = "Player 6", score = 0),
        7 to Player(id = 7, name = "Player 7", score = 0),
        8 to Player(id = 8, name = "Player 8", score = 0),
        9 to Player(id = 9, name = "Player 9", score = 0)
    )

    private var simulationJob: Job? = null

    private val _score: MutableStateFlow<List<Player>> = MutableStateFlow(emptyList())
    val score: StateFlow<List<Player>>
        get() = _score.asStateFlow()

    fun start(scope2: CoroutineScope) {
        simulationJob = scope2.launch(Dispatchers.IO.limitedParallelism(1)) {
            (1..10).map { id ->
                delay(1.seconds)
                launch {
                    updateScore(id = id, score = id + id)
                }
            }.joinAll()
        }
    }

    fun stop() {
        simulationJob?.cancelChildren()
    }

    private fun updateScore(id: Int, score: Int) {
        scores[id]?.copy(score = score)?.let { updatedPlayer ->
            scores[id] = updatedPlayer
        }
        _score.update { scores.map { (_, value) -> value }.sortedByDescending { it.score } }
    }
}