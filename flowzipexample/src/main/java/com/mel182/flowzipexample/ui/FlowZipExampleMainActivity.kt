package com.mel182.flowzipexample.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mel182.flowzipexample.data.LocationTrackerAction
import com.mel182.flowzipexample.domain.LocationObserver
import com.mel182.flowzipexample.ui.theme.BirdExampleTheme

class FlowZipExampleMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BirdExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainTrackingScreen(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding))
                }
            }
        }
    }
}

@Composable
private fun MainTrackingScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: LocationTrackerViewModel =
        viewModel(
            factory = LocationTrackerViewModelFactory(
                locationObserver = LocationObserver(
                    context = context.applicationContext
                )
            )
        )
    val viewState = viewModel.viewState.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsGrantedMap ->
        val fineLocation = permissionsGrantedMap[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocation =
            permissionsGrantedMap[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        viewModel.performAction(
            action = LocationTrackerAction.RequiredLocationPermissionGranted(
                granted = fineLocation && coarseLocation
            )
        )
    }

    Box(modifier = modifier) {

        Column(
            modifier = Modifier.align(alignment = Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (!viewState.value.isTracking && viewState.value.routeData == null) {
                Text(
                    text = "Is not currently tracking"
                )
            } else {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (viewState.value.routeData != null && !viewState.value.isTracking) {
                        val transition = rememberInfiniteTransition(label = "pulse")
                        val alpha by transition.animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulseAlpha"
                        )

                        Text(
                            text = "Stopped",
                            modifier = Modifier.graphicsLayer { this.alpha = alpha }
                        )
                    }


                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Distance:",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = viewState.value.routeData?.let {
                                "${it.distance} m"
                            } ?: run {
                                "loading...."
                            }
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Average speed:",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = viewState.value.routeData?.let {
                                "${it.averageSpeed} kn/h"
                            } ?: run {
                                "loading...."
                            }
                        )
                    }
                }
            }

            if (viewState.value.requiredPermissionGranted) {
                Button(onClick = {
                    viewModel.performAction(action = if (viewState.value.isTracking) LocationTrackerAction.StopTracking else LocationTrackerAction.StartTracking)
                }) {
                    Text(text = if (viewState.value.isTracking) "Stop tracking" else "Start tracking")
                }
            } else {
                Button(onClick = {
                    launcher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }) {
                    Text("Grant permission")
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->

            if (event == Lifecycle.Event.ON_RESUME) {
                val fineLocationPermissionGranted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                val coarseLocationPermissionGranted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                viewModel.performAction(
                    action = LocationTrackerAction.RequiredLocationPermissionGranted(
                        granted = fineLocationPermissionGranted && coarseLocationPermissionGranted
                    )
                )
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}