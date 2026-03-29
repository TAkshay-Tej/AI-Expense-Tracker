package com.example.expensetracker.android.feature.login

import androidx.lifecycle.viewModelScope
import com.example.expensetracker.android.base.BaseViewModel
import com.example.expensetracker.android.base.NavigationEvent
import com.example.expensetracker.android.base.UiEvent
import com.example.expensetracker.android.network.ExpenseApiService
import com.example.expensetracker.android.network.LoginRequest
import com.example.expensetracker.android.network.RegisterRequest
import com.example.expensetracker.android.network.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ExpenseApiService,
    private val tokenManager: TokenManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    override fun onEvent(event: UiEvent) {
        when (event) {
            is LoginUiEvent.OnLoginClicked -> login(event.email, event.password)
            is LoginUiEvent.OnRegisterClicked -> register(event.name, event.email, event.password)
        }
    }

    private fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState(error = "Please fill in all fields")
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val auth = response.body()!!
                    tokenManager.saveToken(auth.token)
                    tokenManager.saveUser(auth.name, auth.email)
                    _navigationEvent.emit(NavigationEvent.NavigateToHome)
                } else {
                    _uiState.value = LoginUiState(error = "Invalid email or password")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState(error = "Connection error. Is the server running?")
            }
        }
    }

    private fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState(error = "Please fill in all fields")
            return
        }
        if (password.length < 6) {
            _uiState.value = LoginUiState(error = "Password must be at least 6 characters")
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            try {
                val response = apiService.register(RegisterRequest(name, email, password))
                if (response.isSuccessful && response.body() != null) {
                    val auth = response.body()!!
                    tokenManager.saveToken(auth.token)
                    tokenManager.saveUser(auth.name, auth.email)
                    _navigationEvent.emit(NavigationEvent.NavigateToHome)
                } else {
                    _uiState.value = LoginUiState(error = "Email already registered")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState(error = "Connection error. Is the server running?")
            }
        }
    }
}

sealed class LoginUiEvent : UiEvent() {
    data class OnLoginClicked(val email: String, val password: String) : LoginUiEvent()
    data class OnRegisterClicked(val name: String, val email: String, val password: String) : LoginUiEvent()
}
