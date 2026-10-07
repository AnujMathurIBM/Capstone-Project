package com.bookworm.app.data.remote

import com.bookworm.app.data.remote.dto.AddCartItemRequest
import com.bookworm.app.data.remote.dto.AddressDto
import com.bookworm.app.data.remote.dto.AddressRequest
import com.bookworm.app.data.remote.dto.AuthResponse
import com.bookworm.app.data.remote.dto.BrandDto
import com.bookworm.app.data.remote.dto.CartDto
import com.bookworm.app.data.remote.dto.CategoryDto
import com.bookworm.app.data.remote.dto.CouponValidateRequest
import com.bookworm.app.data.remote.dto.CouponValidateResponse
import com.bookworm.app.data.remote.dto.GiftCardRedeemRequest
import com.bookworm.app.data.remote.dto.LoginRequest
import com.bookworm.app.data.remote.dto.OrderDto
import com.bookworm.app.data.remote.dto.OrderSummaryDto
import com.bookworm.app.data.remote.dto.PagedResponse
import com.bookworm.app.data.remote.dto.PaymentDto
import com.bookworm.app.data.remote.dto.PaymentRequest
import com.bookworm.app.data.remote.dto.PlaceOrderRequest
import com.bookworm.app.data.remote.dto.ProductDto
import com.bookworm.app.data.remote.dto.ProductSummaryDto
import com.bookworm.app.data.remote.dto.RefreshRequest
import com.bookworm.app.data.remote.dto.RegisterRequest
import com.bookworm.app.data.remote.dto.ReviewDto
import com.bookworm.app.data.remote.dto.ReviewRequest
import com.bookworm.app.data.remote.dto.ShipmentDto
import com.bookworm.app.data.remote.dto.ShippingRateDto
import com.bookworm.app.data.remote.dto.UpdateCartItemRequest
import com.bookworm.app.data.remote.dto.UserDto
import com.bookworm.app.data.remote.dto.WalletDto
import com.bookworm.app.data.remote.dto.WishlistItemDto
import com.bookworm.app.data.remote.dto.WishlistRequest
import retrofit2.Response

class TestApiServiceImpl : ApiService {
    override suspend fun register(request: RegisterRequest): Response<AuthResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun login(request: LoginRequest): Response<AuthResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshToken(request: RefreshRequest): Response<AuthResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getMe(): Response<UserDto> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMe(request: Map<String, String>): Response<UserDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getAddresses(): Response<List<AddressDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun addAddress(request: AddressRequest): Response<AddressDto> {
        TODO("Not yet implemented")
    }

    override suspend fun updateAddress(
        addressId: String,
        request: AddressRequest
    ): Response<AddressDto> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAddress(addressId: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getWishlist(): Response<List<WishlistItemDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun addToWishlist(request: WishlistRequest): Response<WishlistItemDto> {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromWishlist(productId: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getWallet(): Response<WalletDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getCategories(): Response<List<CategoryDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun getBrands(categoryId: String?): Response<List<BrandDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProducts(
        categoryId: String?,
        brandId: String?,
        language: String?,
        format: String?,
        minPrice: Double?,
        maxPrice: Double?,
        search: String?,
        sort: String?,
        page: Int,
        size: Int
    ): Response<PagedResponse<ProductSummaryDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecommended(): Response<List<ProductSummaryDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun getBestsellers(): Response<List<ProductSummaryDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun getNewLaunches(): Response<List<ProductSummaryDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProduct(productId: String): Response<ProductDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getRelatedProducts(productId: String): Response<List<ProductSummaryDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun getReviews(
        productId: String,
        page: Int,
        size: Int
    ): Response<PagedResponse<ReviewDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun submitReview(
        productId: String,
        request: ReviewRequest
    ): Response<ReviewDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getCart(): Response<CartDto> {
        TODO("Not yet implemented")
    }

    override suspend fun addCartItem(request: AddCartItemRequest): Response<CartDto> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCartItem(
        cartItemId: String,
        request: UpdateCartItemRequest
    ): Response<CartDto> {
        TODO("Not yet implemented")
    }

    override suspend fun removeCartItem(cartItemId: String): Response<CartDto> {
        TODO("Not yet implemented")
    }

    override suspend fun clearCart(): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getOrders(): Response<List<OrderSummaryDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun placeOrder(request: PlaceOrderRequest): Response<OrderDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getOrder(orderId: String): Response<OrderDto> {
        TODO("Not yet implemented")
    }

    override suspend fun cancelOrder(orderId: String): Response<OrderDto> {
        TODO("Not yet implemented")
    }

    override suspend fun buyAgain(orderId: String): Response<CartDto> {
        TODO("Not yet implemented")
    }

    override suspend fun initiatePayment(request: PaymentRequest): Response<PaymentDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getPayment(paymentId: String): Response<PaymentDto> {
        TODO("Not yet implemented")
    }

    override suspend fun validateCoupon(request: CouponValidateRequest): Response<CouponValidateResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun redeemGiftCard(request: GiftCardRedeemRequest): Response<WalletDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getShipment(orderId: String): Response<ShipmentDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getShippingRate(orderValue: Double): Response<ShippingRateDto> {
        TODO("Not yet implemented")
    }
}