package com.bookworm.app.feature.checkout.viewmodel

import androidx.lifecycle.viewModelScope
import com.bookworm.app.core.BaseViewModel
import com.bookworm.app.core.UiState
import com.bookworm.app.data.remote.ApiService
import com.bookworm.app.data.remote.dto.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val api: ApiService
) : BaseViewModel() {

    private val _cart      = MutableStateFlow<UiState<CartDto>>(UiState.Idle)
    val cart: StateFlow<UiState<CartDto>> = _cart

    private val _addresses = MutableStateFlow<UiState<List<AddressDto>>>(UiState.Idle)
    val addresses: StateFlow<UiState<List<AddressDto>>> = _addresses

    private val _coupon    = MutableStateFlow<UiState<CouponValidateResponse>>(UiState.Idle)
    val coupon: StateFlow<UiState<CouponValidateResponse>> = _coupon

    private val _order     = MutableStateFlow<UiState<OrderDto>>(UiState.Idle)
    val order: StateFlow<UiState<OrderDto>> = _order

    init {
        loadCart()
        loadAddresses()
    }

    fun loadCart() {
        viewModelScope.launch {
            _cart.value = UiState.Loading
            try {
                val r = api.getCart()
                _cart.value = if (r.isSuccessful && r.body() != null) UiState.Success(r.body()!!)
                              else UiState.Error("Could not load cart")
            } catch (e: Exception) {
                _cart.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun loadAddresses() {
        viewModelScope.launch {
            val r = api.getAddresses()
            if (r.isSuccessful && r.body() != null) _addresses.value = UiState.Success(r.body()!!)
        }
    }

    fun updateItemQuantity(cartItemId: String, quantity: Int) {
        viewModelScope.launch {
            if (quantity <= 0) {
                val r = api.removeCartItem(cartItemId)
                if (r.isSuccessful && r.body() != null) _cart.value = UiState.Success(r.body()!!)
            } else {
                val r = api.updateCartItem(cartItemId, UpdateCartItemRequest(quantity))
                if (r.isSuccessful && r.body() != null) _cart.value = UiState.Success(r.body()!!)
            }
        }
    }

    fun validateCoupon(code: String, orderValue: String) {
        viewModelScope.launch {
            _coupon.value = UiState.Loading
            try {
                val r = api.validateCoupon(CouponValidateRequest(code, orderValue))
                _coupon.value = if (r.isSuccessful && r.body() != null) UiState.Success(r.body()!!)
                                else UiState.Error("Invalid or expired coupon")
            } catch (e: Exception) {
                _coupon.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun placeOrder(addressId: String, couponCode: String?) {
        viewModelScope.launch {
            _order.value = UiState.Loading
            try {
                val r = api.placeOrder(PlaceOrderRequest(addressId, couponCode))
                _order.value = if (r.isSuccessful && r.body() != null) UiState.Success(r.body()!!)
                               else UiState.Error("Could not place order")
            } catch (e: Exception) {
                _order.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    /** Save a new address first, then place the order with the returned addressId. */
    fun placeOrderWithNewAddress(request: AddressRequest, couponCode: String?) {
        viewModelScope.launch {
            _order.value = UiState.Loading
            try {
                val addrResp = api.addAddress(request)
                val addressId = if (addrResp.isSuccessful && addrResp.body() != null) {
                    addrResp.body()!!.addressId
                } else {
                    _order.value = UiState.Error("Could not save address")
                    return@launch
                }
                val r = api.placeOrder(PlaceOrderRequest(addressId, couponCode))
                _order.value = if (r.isSuccessful && r.body() != null) UiState.Success(r.body()!!)
                               else UiState.Error("Could not place order")
            } catch (e: Exception) {
                _order.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun resetOrderState() { _order.value = UiState.Idle }
    fun resetCouponState() { _coupon.value = UiState.Idle }
}
