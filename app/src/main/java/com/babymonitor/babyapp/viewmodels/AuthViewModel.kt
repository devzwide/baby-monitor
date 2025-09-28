package com.babymonitor.babyapp.viewmodels

import androidx.lifecycle.ViewModel
import com.babymonitor.babyapp.controllers.AuthController
import com.babymonitor.babyapp.data.FirebaseAuthManager
import com.babymonitor.babyapp.models.Baby
import com.babymonitor.babyapp.models.Parent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val authManager = FirebaseAuthManager()
    private val authController = AuthController(authManager)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun signIn(email: String, password: String) {
        _authState.value = AuthState.Loading
        authController.handleSignIn(email, password, object : AuthController.AuthCallback {
            override fun onSuccess() {
                _authState.value = AuthState.Success
            }
            override fun onFailure(errorMessage: String) {
                _authState.value = AuthState.Error(errorMessage)
            }
        })
    }

    fun signUp(parent: Parent, baby: Baby, password: String) {
        _authState.value = AuthState.Loading
        authController.handleSignUp(parent, baby, password, object : AuthController.AuthCallback {
            override fun onSuccess() {
                _authState.value = AuthState.Success
            }
            override fun onFailure(errorMessage: String) {
                _authState.value = AuthState.Error(errorMessage)
            }
        })
    }

    fun signOut() {
        authController.handleSignOut()
        _authState.value = AuthState.Idle
    }

    fun isUserSignedIn(): Boolean = authController.isUserSignedIn()
}
