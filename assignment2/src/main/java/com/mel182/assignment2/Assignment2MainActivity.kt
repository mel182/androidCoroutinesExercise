package com.mel182.assignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mel182.assignment2.ui.theme.Assignment2Theme
import com.mel182.assignment2.homework.AssignmentTwoScreen

class Assignment2MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment2Theme {
                AssignmentTwoScreen()
            }
        }
    }
}