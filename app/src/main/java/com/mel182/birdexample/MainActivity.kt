package com.mel182.birdexample

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mel182.birdexample.ui.theme.BirdExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BirdExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BirdMainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


data class Bird(val name: String, val sound: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        name = parcel.readString().orEmpty(),
        sound = parcel.readString().orEmpty()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(sound)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Bird> {
        override fun createFromParcel(parcel: Parcel): Bird = Bird(parcel)
        override fun newArray(size: Int): Array<Bird?> = arrayOfNulls(size)
    }
}

@Composable
private fun BirdMainScreen(modifier: Modifier = Modifier) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val birdList = listOf(
        Bird(name = "Bird 1", sound = "Coo"),
        Bird(name = "Bird 2", sound = "Caw"),
        Bird(name = "Bird 3", sound = "Chirp"),
    )
    val selectedBird = rememberSaveable { mutableStateOf<Bird?>(null) }

    if (isLandscape) {
        BirdScreenLandscape(
            selectedBird = selectedBird.value,
            birds = birdList,
            modifier = modifier.fillMaxSize()
        ) { bird ->
            selectedBird.value = bird
        }
    } else {
        BirdScreenPortrait(
            selectedBird = selectedBird.value,
            birds = birdList,
            modifier = modifier.fillMaxSize()
        ) { bird ->
            selectedBird.value = bird
        }
    }
}


@Composable
private fun BirdScreenPortrait(
    selectedBird: Bird?,
    birds: List<Bird>,
    modifier: Modifier = Modifier,
    onBirdSelected: (Bird) -> Unit
) {

    val context = LocalContext.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp, alignment = Alignment.CenterVertically)
    ) {

        Text(
            text = selectedBird?.name ?: "No bird selected",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            birds.forEach { bird ->
                Button(onClick = {
                    onBirdSelected(bird)
                    println(bird.sound)
                    Toast.makeText(context, bird.sound, Toast.LENGTH_SHORT).show()
                }) {
                    Text(bird.name)
                }
            }
        }
    }
}

@Composable
private fun BirdScreenLandscape(
    selectedBird: Bird?,
    birds: List<Bird>,
    modifier: Modifier = Modifier,
    onBirdSelected: (Bird) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, alignment = Alignment.CenterVertically)
    ) {

        Text(
            text = selectedBird?.name ?: "No bird selected",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

            birds.forEach { bird ->
                Button(onClick = {
                    onBirdSelected(bird)
                    println(bird.sound)
                    Toast.makeText(context, bird.sound, Toast.LENGTH_SHORT).show()
                }) {
                    Text(bird.name)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BirdExampleTheme {
        Greeting("Android")
    }
}