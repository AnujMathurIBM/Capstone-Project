package com.bookworm.app.feature.auth.viewmodel

import androidx.lifecycle.viewModelScope
import com.bookworm.app.core.BaseViewModel
import com.bookworm.app.core.UiState
import com.bookworm.app.data.local.TokenRepository
import com.bookworm.app.data.remote.ApiService
import com.bookworm.app.data.remote.dto.AuthResponse
import com.bookworm.app.data.remote.dto.LoginRequest
import com.bookworm.app.data.remote.dto.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: ApiService,
    private val tokenRepository: TokenRepository
) : BaseViewModel() {

    private val _loginState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle)
    val loginState: StateFlow<UiState<AuthResponse>> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val response = api.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val auth = response.body()!!
                    tokenRepository.saveTokens(auth.accessToken, auth.refreshToken)
                    _loginState.value = UiState.Success(auth)
                } else {
                    _loginState.value = UiState.Error(
                        message = "Invalid email or password",
                        code = response.code()
                    )
                }
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun resetState() {
        _loginState.value = UiState.Idle
    }
}
