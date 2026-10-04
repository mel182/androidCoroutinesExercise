package com.mel182.flowcombineexample.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mel182.flowcombineexample.ui.theme.BirdExampleTheme

class FlowCombineExampleMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BirdExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CombineExampleView(
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
private fun CombineExampleView(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val viewModel: FormExampleViewModel =
        viewModel(
            factory = FormExampleViewModelFactory()
        )

    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val canRegister by viewModel.canRegister.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TextField(
            value = email,
            onValueChange = viewModel::onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Email") }
        )

        TextField(
            value = password,
            onValueChange = viewModel::onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Password") }
        )

        Button(
            onClick = {
                Toast.makeText(context, "Register button clicked!", Toast.LENGTH_SHORT).show()
            },
            enabled = canRegister
        ) {
            Text(text = "Register")
        }
    }
}