package com.bookworm.app.data.remote

import com.bookworm.app.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ─── Auth ──────────────────────────────────────────────────────────────────
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<AuthResponse>

    // ─── Users ─────────────────────────────────────────────────────────────────
    @GET("users/me")
    suspend fun getMe(): Response<UserDto>

    @PUT("users/me")
    suspend fun updateMe(@Body request: Map<String, String>): Response<UserDto>

    @GET("users/me/addresses")
    suspend fun getAddresses(): Response<List<AddressDto>>

    @POST("users/me/addresses")
    suspend fun addAddress(@Body request: AddressRequest): Response<AddressDto>

    @PUT("users/me/addresses/{addressId}")
    suspend fun updateAddress(
        @Path("addressId") addressId: String,
        @Body request: AddressRequest
    ): Response<AddressDto>

    @DELETE("users/me/addresses/{addressId}")
    suspend fun deleteAddress(@Path("addressId") addressId: String): Response<Unit>

    @GET("users/me/wishlist")
    suspend fun getWishlist(): Response<List<WishlistItemDto>>

    @POST("users/me/wishlist")
    suspend fun addToWishlist(@Body request: WishlistRequest): Response<WishlistItemDto>

    @DELETE("users/me/wishlist/{productId}")
    suspend fun removeFromWishlist(@Path("productId") productId: String): Response<Unit>

    @GET("users/me/wallet")
    suspend fun getWallet(): Response<WalletDto>

    // ─── Catalog ───────────────────────────────────────────────────────────────
    @GET("catalog/categories")
    suspend fun getCategories(): Response<List<CategoryDto>>

    @GET("catalog/brands")
    suspend fun getBrands(
        @Query("categoryId") categoryId: String? = null
    ): Response<List<BrandDto>>

    @GET("catalog/products")
    suspend fun getProducts(
        @Query("categoryId") categoryId: String? = null,
        @Query("brandId") brandId: String? = null,
        @Query("language") language: String? = null,
        @Query("format") format: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("search") search: String? = null,
        @Query("sort") sort: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PagedResponse<ProductSummaryDto>>

    @GET("catalog/products/recommended")
    suspend fun getRecommended(): Response<List<ProductSummaryDto>>

    @GET("catalog/products/bestsellers")
    suspend fun getBestsellers(): Response<List<ProductSummaryDto>>

    @GET("catalog/products/new-launches")
    suspend fun getNewLaunches(): Response<List<ProductSummaryDto>>

    @GET("catalog/products/{productId}")
    suspend fun getProduct(@Path("productId") productId: String): Response<ProductDto>

    @GET("catalog/products/{productId}/related")
    suspend fun getRelatedProducts(@Path("productId") productId: String): Response<List<ProductSummaryDto>>

    @GET("catalog/products/{productId}/reviews")
    suspend fun getReviews(
        @Path("productId") productId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<PagedResponse<ReviewDto>>

    @POST("catalog/products/{productId}/reviews")
    suspend fun submitReview(
        @Path("productId") productId: String,
        @Body request: ReviewRequest
    ): Response<ReviewDto>

    // ─── Cart ──────────────────────────────────────────────────────────────────
    @GET("cart")
    suspend fun getCart(): Response<CartDto>

    @POST("cart/items")
    suspend fun addCartItem(@Body request: AddCartItemRequest): Response<CartDto>

    @PUT("cart/items/{cartItemId}")
    suspend fun updateCartItem(
        @Path("cartItemId") cartItemId: String,
        @Body request: UpdateCartItemRequest
    ): Response<CartDto>

    @DELETE("cart/items/{cartItemId}")
    suspend fun removeCartItem(@Path("cartItemId") cartItemId: String): Response<CartDto>

    @DELETE("cart")
    suspend fun clearCart(): Response<Unit>

    // ─── Orders ────────────────────────────────────────────────────────────────
    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("status") status: String? = null
    ): Response<PagedResponse<OrderDto>>

    @POST("orders")
    suspend fun placeOrder(@Body request: PlaceOrderRequest): Response<OrderDto>

    @GET("orders/{orderId}")
    suspend fun getOrder(@Path("orderId") orderId: String): Response<OrderDto>

    @POST("orders/{orderId}/cancel")
    suspend fun cancelOrder(@Path("orderId") orderId: String): Response<OrderDto>

    @POST("orders/{orderId}/buy-again")
    suspend fun buyAgain(@Path("orderId") orderId: String): Response<CartDto>

    // ─── Payments ──────────────────────────────────────────────────────────────
    @POST("payments")
    suspend fun initiatePayment(@Body request: PaymentRequest): Response<PaymentDto>

    @GET("payments/{paymentId}")
    suspend fun getPayment(@Path("paymentId") paymentId: String): Response<PaymentDto>

    @POST("payments/coupons/validate")
    suspend fun validateCoupon(@Body request: CouponValidateRequest): Response<CouponValidateResponse>

    @POST("payments/gift-cards/redeem")
    suspend fun redeemGiftCard(@Body request: GiftCardRedeemRequest): Response<WalletDto>

    // ─── Shipping ──────────────────────────────────────────────────────────────
    @GET("shipments/{orderId}")
    suspend fun getShipment(@Path("orderId") orderId: String): Response<ShipmentDto>

    @GET("shipping/rates")
    suspend fun getShippingRate(@Query("orderValue") orderValue: Double): Response<ShippingRateDto>
}
