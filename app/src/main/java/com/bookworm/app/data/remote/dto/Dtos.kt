package com.bookworm.app.data.remote.dto

import com.google.gson.annotations.SerializedName

// ─── Auth ─────────────────────────────────────────────────────────────────────

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("phone") val phone: String? = null
)

data class RefreshRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

data class AuthResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("tokenType") val tokenType: String,
    @SerializedName("expiresIn") val expiresIn: Int,
    @SerializedName("user") val user: UserDto
)

// ─── User ─────────────────────────────────────────────────────────────────────

data class UserDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("email") val email: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("role") val role: String,
    @SerializedName("createdAt") val createdAt: String
)

data class AddressDto(
    @SerializedName("addressId") val addressId: String,
    @SerializedName("label") val label: String?,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("line1") val line1: String,
    @SerializedName("line2") val line2: String?,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("country") val country: String,
    @SerializedName("pin") val pin: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("isDefault") val isDefault: Boolean
)

data class AddressRequest(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("line1") val line1: String,
    @SerializedName("line2") val line2: String? = null,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("country") val country: String = "India",
    @SerializedName("pin") val pin: String,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("isDefault") val isDefault: Boolean = false,
    @SerializedName("label") val label: String? = null
)

data class WalletDto(
    @SerializedName("walletId") val walletId: String,
    @SerializedName("balance") val balance: String,
    @SerializedName("updatedAt") val updatedAt: String
)

// ─── Catalog ──────────────────────────────────────────────────────────────────

data class CategoryDto(
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("parentCategoryId") val parentCategoryId: String?,
    @SerializedName("sortOrder") val sortOrder: Int,
    @SerializedName("children") val children: List<CategoryDto>?
)

data class BrandDto(
    @SerializedName("brandId") val brandId: String,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("bio") val bio: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?
)

data class ProductSummaryDto(
    @SerializedName("productId") val productId: String,
    @SerializedName("title") val title: String,
    @SerializedName("authorName") val authorName: String,
    @SerializedName("format") val format: String,
    @SerializedName("price") val price: String,
    @SerializedName("salePrice") val salePrice: String?,
    @SerializedName("coverImageUrl") val coverImageUrl: String?,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("ratingAvg") val ratingAvg: Float,
    @SerializedName("deliveryDate") val deliveryDate: String?
)

data class ProductImageDto(
    @SerializedName("imageId") val imageId: String,
    @SerializedName("url") val url: String,
    @SerializedName("isCover") val isCover: Boolean,
    @SerializedName("sortOrder") val sortOrder: Int
)

data class ProductDto(
    @SerializedName("productId") val productId: String,
    @SerializedName("title") val title: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("description") val description: String?,
    @SerializedName("authorBio") val authorBio: String?,
    @SerializedName("publisher") val publisher: String?,
    @SerializedName("isbn") val isbn: String?,
    @SerializedName("pages") val pages: Int?,
    @SerializedName("brand") val brand: BrandDto?,
    @SerializedName("category") val category: CategoryDto,
    @SerializedName("format") val format: String,
    @SerializedName("language") val language: String,
    @SerializedName("price") val price: String,
    @SerializedName("salePrice") val salePrice: String?,
    @SerializedName("stockQty") val stockQty: Int,
    @SerializedName("ratingAvg") val ratingAvg: Float,
    @SerializedName("sellCount") val sellCount: Int,
    @SerializedName("isFeatured") val isFeatured: Boolean,
    @SerializedName("images") val images: List<ProductImageDto>,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("deliveryDate") val deliveryDate: String?,
    @SerializedName("createdAt") val createdAt: String
)

data class ReviewDto(
    @SerializedName("reviewId") val reviewId: String,
    @SerializedName("productId") val productId: String,
    @SerializedName("user") val user: ReviewUserDto,
    @SerializedName("rating") val rating: Int,
    @SerializedName("body") val body: String?,
    @SerializedName("createdAt") val createdAt: String
)

data class ReviewUserDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String
)

data class ReviewRequest(
    @SerializedName("rating") val rating: Int,
    @SerializedName("body") val body: String? = null
)

// ─── Cart ─────────────────────────────────────────────────────────────────────

data class CartItemDto(
    @SerializedName("cartItemId") val cartItemId: String,
    @SerializedName("product") val product: ProductSummaryDto,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("addedAt") val addedAt: String
)

data class CartDto(
    @SerializedName("cartId") val cartId: String,
    @SerializedName("items") val items: List<CartItemDto>,
    @SerializedName("itemCount") val itemCount: Int,
    @SerializedName("subtotal") val subtotal: String,
    @SerializedName("updatedAt") val updatedAt: String
)

data class AddCartItemRequest(
    @SerializedName("productId") val productId: String,
    @SerializedName("quantity") val quantity: Int
)

data class UpdateCartItemRequest(
    @SerializedName("quantity") val quantity: Int
)

// ─── Orders ───────────────────────────────────────────────────────────────────

data class PlaceOrderRequest(
    @SerializedName("addressId") val addressId: String,
    @SerializedName("couponCode") val couponCode: String? = null
)

data class OrderItemDto(
    @SerializedName("orderItemId") val orderItemId: String,
    @SerializedName("product") val product: ProductSummaryDto,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unitPrice") val unitPrice: String,
    @SerializedName("format") val format: String
)

data class OrderDto(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("status") val status: String,
    @SerializedName("items") val items: List<OrderItemDto>,
    @SerializedName("address") val address: AddressDto,
    @SerializedName("subtotal") val subtotal: String,
    @SerializedName("tax") val tax: String,
    @SerializedName("deliveryCharges") val deliveryCharges: String,
    @SerializedName("discount") val discount: String,
    @SerializedName("payableAmount") val payableAmount: String,
    @SerializedName("couponCode") val couponCode: String?,
    @SerializedName("cancelDeadline") val cancelDeadline: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

// ─── Payments ─────────────────────────────────────────────────────────────────

data class PaymentRequest(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("method") val method: String,
    @SerializedName("cardNumber") val cardNumber: String? = null,
    @SerializedName("nameOnCard") val nameOnCard: String? = null,
    @SerializedName("cvv") val cvv: String? = null,
    @SerializedName("expiryDate") val expiryDate: String? = null,
    @SerializedName("upiId") val upiId: String? = null
)

data class PaymentDto(
    @SerializedName("paymentId") val paymentId: String,
    @SerializedName("orderId") val orderId: String,
    @SerializedName("method") val method: String,
    @SerializedName("status") val status: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("gatewayRef") val gatewayRef: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

data class CouponValidateRequest(
    @SerializedName("code") val code: String,
    @SerializedName("orderValue") val orderValue: String
)

data class CouponValidateResponse(
    @SerializedName("couponId") val couponId: String,
    @SerializedName("code") val code: String,
    @SerializedName("discountType") val discountType: String,
    @SerializedName("discountValue") val discountValue: String,
    @SerializedName("discountAmount") val discountAmount: String
)

data class GiftCardRedeemRequest(
    @SerializedName("code") val code: String
)

// ─── Shipping ─────────────────────────────────────────────────────────────────

data class ShipmentDto(
    @SerializedName("shipmentId") val shipmentId: String,
    @SerializedName("orderId") val orderId: String,
    @SerializedName("trackingNumber") val trackingNumber: String?,
    @SerializedName("carrier") val carrier: String?,
    @SerializedName("status") val status: String,
    @SerializedName("estimatedDelivery") val estimatedDelivery: String?,
    @SerializedName("shippedAt") val shippedAt: String?,
    @SerializedName("deliveredAt") val deliveredAt: String?
)

data class ShippingRateDto(
    @SerializedName("rateId") val rateId: String,
    @SerializedName("rate") val rate: String,
    @SerializedName("estimatedDays") val estimatedDays: Int,
    @SerializedName("isFreeDelivery") val isFreeDelivery: Boolean
)

// ─── Paged Response ───────────────────────────────────────────────────────────

data class PagedResponse<T>(
    @SerializedName("content") val content: List<T>,
    @SerializedName("page") val page: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("totalElements") val totalElements: Long,
    @SerializedName("totalPages") val totalPages: Int
)

// ─── Wishlist ─────────────────────────────────────────────────────────────────

data class WishlistItemDto(
    @SerializedName("wishlistId") val wishlistId: String,
    @SerializedName("product") val product: ProductSummaryDto,
    @SerializedName("addedAt") val addedAt: String
)

data class WishlistRequest(
    @SerializedName("productId") val productId: String
)
