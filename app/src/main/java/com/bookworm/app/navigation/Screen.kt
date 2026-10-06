package com.bookworm.app.navigation

/**
 * Sealed class representing all navigation destinations in the app.
 * Each object/class holds the route string and any required arguments.
 */
sealed class Screen(val route: String) {

    /** Login / authentication entry point. Optional [returnTo] route to navigate after login. */
    object Login : Screen("login?returnTo={returnTo}") {
        fun route(returnTo: String? = null) =
            if (returnTo != null) "login?returnTo=${returnTo}" else "login"
        const val ARG_RETURN_TO = "returnTo"
    }

    /** Home / Landing screen */
    object Landing : Screen("landing")

    /** Category-filtered catalog screen */
    object Catalog : Screen("catalog?categoryId={categoryId}") {
        fun createRoute(categoryId: String? = null) =
            if (categoryId != null) "catalog?categoryId=$categoryId" else "catalog"
        const val ARG_CATEGORY_ID = "categoryId"
    }

    /** Full product detail screen */
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
        const val ARG_PRODUCT_ID = "productId"
    }

    /** Shopping cart + checkout screen */
    object Checkout : Screen("checkout")

    /** Payment method selection screen */
    object Payment : Screen("payment/{orderId}") {
        fun createRoute(orderId: String) = "payment/$orderId"
        const val ARG_ORDER_ID = "orderId"
    }

    /** Order success / confirmation screen */
    object OrderSuccess : Screen("order-success/{orderId}") {
        fun createRoute(orderId: String) = "order-success/$orderId"
        const val ARG_ORDER_ID = "orderId"
    }

    /** Order history screen */
    object OrderHistory : Screen("order-history")

    /** Wishlist screen */
    object Wishlist : Screen("wishlist")
}
