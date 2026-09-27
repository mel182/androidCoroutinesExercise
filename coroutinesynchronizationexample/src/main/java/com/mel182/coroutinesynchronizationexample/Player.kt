package com.mel182.coroutinesynchronizationexample

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Player(val id: Int, val name: String, var score: Int) : Parcelable