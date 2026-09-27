package com.mel182.coroutinesynchronizationexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mel182.coroutinesynchronizationexample.ui.theme.CoroutineSyncTheme

class CoroutineSyncMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoroutineSyncTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LeaderboardMainScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardMainScreen(modifier: Modifier = Modifier) {

    val viewModel: LeaderBoardViewModel =
        viewModel(factory = LeaderBoardViewModelFactory(repo = LeaderBoardRepository()))
    val leaderBoardViewState = viewModel.leaderboardViewState.collectAsStateWithLifecycle()

    Box(modifier = modifier) {

        Column(Modifier.fillMaxSize()) {

            Text(
                "Leaderboard",
                modifier = Modifier.padding(all = 16.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )

            LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {

                itemsIndexed(leaderBoardViewState.value.scoreList) { index, player ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(player.name)

                        Text("${player.score}", fontWeight = FontWeight.Bold)
                    }

                    if (index < leaderBoardViewState.value.scoreList.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }



        Button(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            onClick = {
                viewModel.performAction(action = if (leaderBoardViewState.value.isSimulating) LeaderboardViewModelAction.ToggleSimulation(simulate = false) else LeaderboardViewModelAction.ToggleSimulation(simulate = true))
        }) {

            Text(text = if (leaderBoardViewState.value.isSimulating) "Stop" else "Start")
        }
    }
}