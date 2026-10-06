package com.bookworm.app.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response

/**
 * Base ViewModel providing a helper to safely execute API calls and
 * map results into [UiState].
 */
abstract class BaseViewModel : ViewModel() {

    protected fun <T> MutableStateFlow<UiState<T>>.load(
        block: suspend () -> Response<T>
    ): suspend () -> Unit = {
        value = UiState.Loading
        try {
            val response = block()
            value = if (response.isSuccessful && response.body() != null) {
                UiState.Success(response.body()!!)
            } else {
                UiState.Error(
                    message = response.errorBody()?.string() ?: "Unknown error",
                    code = response.code()
                )
            }
        } catch (e: Exception) {
            value = UiState.Error(e.message ?: "Network error")
        }
    }
}
