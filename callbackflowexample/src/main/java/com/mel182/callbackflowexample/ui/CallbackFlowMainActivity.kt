package com.mel182.callbackflowexample.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mel182.callbackflowexample.domain.LocationObserver
import com.mel182.callbackflowexample.data.LocationTrackerAction
import com.mel182.callbackflowexample.ui.theme.CallbackFlowExampleTheme

/**
 * This example provide a practical example on how to use
 * the callback flow in a Android app.
 *
 * In this example, we will be implementing a location tracking.
 * The setup requires adding a dependency for Google Location Tracking Services
 * to access the FusedLocationProviderClient, which is necessary to track the user's location.
 *  ------ A class called LocationObserver is created to manage this functionality.
 *
 * Note:
 * - The CallbackFlow is used to convert location callbacks into flow emissions.
 * - An ObserveLocation function is created to track location updates at specific intervals.
 * - This section involves setting up a LocationManager to check GPS or network provider availability.
 *
 * The code handles permissions, constructs a LocationRequest, and defines a LocationCallback to send
 * new location updates as emissions into the flow.
 *
 * Utilizing awaitClose for Flow Management
 * The awaitClose function ensures that the flow remains active for as long as needed, and it
 * automatically unregisters callbacks when the flow is closed.
 * This approach minimizes the risk of not unregistering callbacks properly.
 *
 * This example implementation manages the state and ensures proper callback registration and deregistration
 * efficiently based on lifecycle scope.
 *
 */
class CallbackFlowMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CallbackFlowExampleTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    topBar = {
                        Text(
                            text = "Location coordinate",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }) { innerPadding ->
                    CounterScreen(
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
fun CounterScreen(modifier: Modifier = Modifier) {

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

            if (!viewState.value.isTracking) {
                Text(
                    text = "Is not currently tracking"
                )
            } else {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Lat:",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${viewState.value.locationData?.lat ?: "loading...."}"
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Lng:",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${viewState.value.locationData?.lng ?: "loading...."}"
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Altitude:",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${viewState.value.locationData?.altitude?.let { "%.2f".format(it) } ?: "-"} m"
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