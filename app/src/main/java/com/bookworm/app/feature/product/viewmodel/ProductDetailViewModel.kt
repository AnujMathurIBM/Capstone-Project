package com.bookworm.app.feature.product.viewmodel

import androidx.lifecycle.viewModelScope
import com.bookworm.app.core.BaseViewModel
import com.bookworm.app.core.UiState
import com.bookworm.app.data.remote.ApiService
import com.bookworm.app.data.remote.dto.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val api: ApiService
) : BaseViewModel() {

    private val _product  = MutableStateFlow<UiState<ProductDto>>(UiState.Idle)
    val product: StateFlow<UiState<ProductDto>> = _product

    private val _related  = MutableStateFlow<UiState<List<ProductSummaryDto>>>(UiState.Idle)
    val related: StateFlow<UiState<List<ProductSummaryDto>>> = _related

    private val _reviews  = MutableStateFlow<UiState<PagedResponse<ReviewDto>>>(UiState.Idle)
    val reviews: StateFlow<UiState<PagedResponse<ReviewDto>>> = _reviews

    private val _cartAction = MutableStateFlow<UiState<CartDto>>(UiState.Idle)
    val cartAction: StateFlow<UiState<CartDto>> = _cartAction

    private val _wishlistAction = MutableStateFlow<UiState<WishlistItemDto>>(UiState.Idle)
    val wishlistAction: StateFlow<UiState<WishlistItemDto>> = _wishlistAction

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _product.value = UiState.Loading
            _related.value = UiState.Loading
            _reviews.value = UiState.Loading

            val prodDef    = async { api.getProduct(productId) }
            val relDef     = async { api.getRelatedProducts(productId) }
            val reviewsDef = async { api.getReviews(productId) }

            prodDef.await().let    { r -> _product.value  = if (r.isSuccessful && r.body() != null) UiState.Success(r.body()!!) else UiState.Error("Product not found") }
            relDef.await().let     { r -> _related.value  = if (r.isSuccessful && r.body() != null) UiState.Success(r.body()!!) else UiState.Error("") }
            reviewsDef.await().let { r -> _reviews.value  = if (r.isSuccessful && r.body() != null) UiState.Success(r.body()!!) else UiState.Error("") }
        }
    }

    fun addToCart(productId: String, quantity: Int = 1) {
        viewModelScope.launch {
            _cartAction.value = UiState.Loading
            try {
                val response = api.addCartItem(AddCartItemRequest(productId, quantity))
                _cartAction.value = if (response.isSuccessful && response.body() != null)
                    UiState.Success(response.body()!!)
                else UiState.Error("Could not add to cart")
            } catch (e: Exception) {
                _cartAction.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun addToWishlist(productId: String) {
        viewModelScope.launch {
            _wishlistAction.value = UiState.Loading
            try {
                val response = api.addToWishlist(WishlistRequest(productId))
                _wishlistAction.value = if (response.isSuccessful && response.body() != null)
                    UiState.Success(response.body()!!)
                else UiState.Error("Could not add to wishlist")
            } catch (e: Exception) {
                _wishlistAction.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun submitReview(productId: String, rating: Int, body: String?) {
        viewModelScope.launch {
            try {
                api.submitReview(productId, ReviewRequest(rating, body))
                // Reload reviews after submission
                val r = api.getReviews(productId)
                if (r.isSuccessful && r.body() != null) _reviews.value = UiState.Success(r.body()!!)
            } catch (_: Exception) {}
        }
    }

    fun resetCartAction() { _cartAction.value = UiState.Idle }
    fun resetWishlistAction() { _wishlistAction.value = UiState.Idle }
}
