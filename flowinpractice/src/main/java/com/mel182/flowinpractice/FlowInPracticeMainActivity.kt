package com.mel182.flowinpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mel182.flowinpractice.ui.theme.BirdExampleTheme

class FlowInPracticeMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BirdExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val viewModel = viewModel<TimerViewModel>()
                    val formattedTime = viewModel.formattedTime.collectAsStateWithLifecycle()
                    val progress = viewModel.progress.collectAsStateWithLifecycle()

                    TimerView(
                        time = formattedTime,
                        progress = progress,
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
private fun TimerView(
    time: State<String>,
    progress: State<Float>,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically)
    ) {

        Text(time.value)

        LinearProgressIndicator(
            progress = { progress.value },
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth()
        )
    }
}
