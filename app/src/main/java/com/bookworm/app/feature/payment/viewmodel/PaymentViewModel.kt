package com.bookworm.app.feature.payment.viewmodel

import androidx.lifecycle.viewModelScope
import com.bookworm.app.core.BaseViewModel
import com.bookworm.app.core.UiState
import com.bookworm.app.data.remote.ApiService
import com.bookworm.app.data.remote.dto.OrderDto
import com.bookworm.app.data.remote.dto.PaymentDto
import com.bookworm.app.data.remote.dto.PaymentRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PaymentMethod { CREDIT_CARD, DEBIT_CARD, UPI, WALLET }

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val api: ApiService
) : BaseViewModel() {

    private val _order         = MutableStateFlow<UiState<OrderDto>>(UiState.Idle)
    val order: StateFlow<UiState<OrderDto>> = _order

    private val _paymentResult = MutableStateFlow<UiState<PaymentDto>>(UiState.Idle)
    val paymentResult: StateFlow<UiState<PaymentDto>> = _paymentResult

    var selectedMethod = MutableStateFlow(PaymentMethod.CREDIT_CARD)

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _order.value = UiState.Loading
            try {
                val r = api.getOrder(orderId)
                _order.value = if (r.isSuccessful && r.body() != null) UiState.Success(r.body()!!)
                               else UiState.Error("Could not load order")
            } catch (e: Exception) {
                _order.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun pay(
        orderId: String,
        method: PaymentMethod,
        cardNumber: String? = null,
        nameOnCard: String? = null,
        cvv: String? = null,
        expiryDate: String? = null,
        upiId: String? = null
    ) {
        viewModelScope.launch {
            _paymentResult.value = UiState.Loading
            try {
                val r = api.initiatePayment(
                    PaymentRequest(
                        orderId    = orderId,
                        method     = method.name,
                        cardNumber = cardNumber,
                        nameOnCard = nameOnCard,
                        cvv        = cvv,
                        expiryDate = expiryDate,
                        upiId      = upiId
                    )
                )
                _paymentResult.value = if (r.isSuccessful && r.body() != null)
                    UiState.Success(r.body()!!)
                else UiState.Error("Payment failed. Please try again.")
            } catch (e: Exception) {
                _paymentResult.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun resetPaymentResult() { _paymentResult.value = UiState.Idle }
}
