package com.bookworm.app.feature.orders.viewmodel

import androidx.lifecycle.viewModelScope
import com.bookworm.app.core.BaseViewModel
import com.bookworm.app.core.UiState
import com.bookworm.app.data.remote.ApiService
import com.bookworm.app.data.remote.dto.CartDto
import com.bookworm.app.data.remote.dto.OrderDto
import com.bookworm.app.data.remote.dto.OrderSummaryDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val api: ApiService
) : BaseViewModel() {

    private val _order       = MutableStateFlow<UiState<OrderDto>>(UiState.Idle)
    val order: StateFlow<UiState<OrderDto>> = _order

    private val _orderHistory = MutableStateFlow<UiState<List<OrderSummaryDto>>>(UiState.Idle)
    val orderHistory: StateFlow<UiState<List<OrderSummaryDto>>> = _orderHistory

    private val _cancelState = MutableStateFlow<UiState<OrderDto>>(UiState.Idle)
    val cancelState: StateFlow<UiState<OrderDto>> = _cancelState

    private val _buyAgainState = MutableStateFlow<UiState<CartDto>>(UiState.Idle)
    val buyAgainState: StateFlow<UiState<CartDto>> = _buyAgainState

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _order.value = UiState.Loading
            try {
                val r = api.getOrder(orderId)
                _order.value = if (r.isSuccessful && r.body() != null) UiState.Success(r.body()!!)
                               else UiState.Error("Order not found")
            } catch (e: Exception) {
                _order.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun loadOrderHistory() {
        viewModelScope.launch {
            _orderHistory.value = UiState.Loading
            try {
                val r = api.getOrders()
                _orderHistory.value = if (r.isSuccessful && r.body() != null) UiState.Success(r.body()!!)
                                      else UiState.Error("Could not load orders")
            } catch (e: Exception) {
                _orderHistory.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            _cancelState.value = UiState.Loading
            try {
                val r = api.cancelOrder(orderId)
                _cancelState.value = if (r.isSuccessful && r.body() != null) UiState.Success(r.body()!!)
                                     else UiState.Error("Could not cancel order")
                // Refresh history
                loadOrderHistory()
            } catch (e: Exception) {
                _cancelState.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun buyAgain(orderId: String) {
        viewModelScope.launch {
            _buyAgainState.value = UiState.Loading
            try {
                val r = api.buyAgain(orderId)
                _buyAgainState.value = if (r.isSuccessful && r.body() != null) UiState.Success(r.body()!!)
                                       else UiState.Error("Could not add to cart")
            } catch (e: Exception) {
                _buyAgainState.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun resetCancelState()   { _cancelState.value    = UiState.Idle }
    fun resetBuyAgainState() { _buyAgainState.value  = UiState.Idle }
}
