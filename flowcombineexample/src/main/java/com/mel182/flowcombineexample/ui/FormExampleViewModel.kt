package com.mel182.flowcombineexample.ui

import android.util.Log
import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.milliseconds

class FormExampleViewModel : ViewModel() {


    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    // Flow combine example
    @OptIn(FlowPreview::class)
    val canRegister = email
        .debounce(500.milliseconds)
        .combine(password) { email, password ->
            val isValidEmail = PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
            val isValidPassword = password.any { !it.isLetterOrDigit() } && password.length > 9

            isValidEmail && isValidPassword
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )

    fun onEmailChange(email: String) {
        Log.i("TAG35", "Email: $email")
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        Log.i("TAG35", "Password: $password")
        _password.value = password
    }
}