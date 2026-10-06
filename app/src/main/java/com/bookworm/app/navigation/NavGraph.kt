package com.bookworm.app.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bookworm.app.data.local.TokenRepository
import com.bookworm.app.feature.auth.ui.LoginScreen
import com.bookworm.app.feature.catalog.ui.CatalogScreen
import com.bookworm.app.feature.checkout.ui.CheckoutScreen
import com.bookworm.app.feature.landing.ui.LandingScreen
import com.bookworm.app.feature.orders.ui.OrderHistoryScreen
import com.bookworm.app.feature.orders.ui.OrderSuccessScreen
import com.bookworm.app.feature.payment.ui.PaymentScreen
import com.bookworm.app.feature.product.ui.ProductDetailScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// ── Thin ViewModel to expose login state to the NavHost ──────────────────────
@HiltViewModel
class AuthStateViewModel @Inject constructor(
    tokenRepository: TokenRepository
) : ViewModel() {
    val isLoggedIn = tokenRepository.accessToken.map { !it.isNullOrBlank() }
}

@Composable
fun BookWormNavHost() {
    val navController = rememberNavController()
    val authStateViewModel: AuthStateViewModel = hiltViewModel()
    val isLoggedIn by authStateViewModel.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)

    // Guard: navigate to a protected route only if logged in, else go to Login with returnTo
    fun NavHostController.navigateAuthenticated(route: String) {
        if (isLoggedIn) {
            navigate(route)
        } else {
            navigate(Screen.Login.route(returnTo = route)) {
                // Don't stack multiple Login screens if already on one
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route(returnTo = null)
    ) {

        // ── Auth ──────────────────────────────────────────────────────────
        composable(
            route = Screen.Login.route,
            arguments = listOf(
                navArgument(Screen.Login.ARG_RETURN_TO) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val returnTo = backStackEntry.arguments?.getString(Screen.Login.ARG_RETURN_TO)
            LoginScreen(
                onLoginSuccess = {
                    if (!returnTo.isNullOrBlank()) {
                        // Go to the screen the user originally wanted
                        navController.navigate(returnTo) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Landing.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onGuestContinue = {
                    navController.navigate(Screen.Landing.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Landing ───────────────────────────────────────────────────────
        composable(Screen.Landing.route) {
            LandingScreen(
                onCategorySelected = { categoryId ->
                    navController.navigate(Screen.Catalog.createRoute(categoryId))
                },
                onProductClicked = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onCartClicked = {
                    navController.navigateAuthenticated(Screen.Checkout.route)
                },
                onOrderHistoryClicked = {
                    navController.navigateAuthenticated(Screen.OrderHistory.route)
                }
            )
        }

        // ── Catalog ───────────────────────────────────────────────────────
        composable(
            route = Screen.Catalog.route,
            arguments = listOf(
                navArgument(Screen.Catalog.ARG_CATEGORY_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString(Screen.Catalog.ARG_CATEGORY_ID)
            CatalogScreen(
                categoryId = categoryId,
                onProductClicked = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onBackClicked = { navController.popBackStack() }
            )
        }

        // ── Product Detail ────────────────────────────────────────────────
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument(Screen.ProductDetail.ARG_PRODUCT_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments!!.getString(Screen.ProductDetail.ARG_PRODUCT_ID)!!
            ProductDetailScreen(
                productId = productId,
                onBackClicked = { navController.popBackStack() },
                onGoToCart = {
                    navController.navigateAuthenticated(Screen.Checkout.route)
                }
            )
        }

        // ── Checkout ──────────────────────────────────────────────────────
        composable(Screen.Checkout.route) {
            CheckoutScreen(
                onBackClicked = { navController.popBackStack() },
                onPayNow = { orderId ->
                    navController.navigate(Screen.Payment.createRoute(orderId))
                }
            )
        }

        // ── Payment ───────────────────────────────────────────────────────
        composable(
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument(Screen.Payment.ARG_ORDER_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments!!.getString(Screen.Payment.ARG_ORDER_ID)!!
            PaymentScreen(
                orderId = orderId,
                onPaymentSuccess = { paidOrderId ->
                    navController.navigate(Screen.OrderSuccess.createRoute(paidOrderId)) {
                        popUpTo(Screen.Checkout.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Order Success ─────────────────────────────────────────────────
        composable(
            route = Screen.OrderSuccess.route,
            arguments = listOf(
                navArgument(Screen.OrderSuccess.ARG_ORDER_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments!!.getString(Screen.OrderSuccess.ARG_ORDER_ID)!!
            OrderSuccessScreen(
                orderId = orderId,
                onContinueShopping = {
                    navController.navigate(Screen.Landing.route) {
                        popUpTo(Screen.Landing.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Order History ─────────────────────────────────────────────────
        composable(Screen.OrderHistory.route) {
            OrderHistoryScreen(
                onBackClicked = { navController.popBackStack() },
                onBuyAgain = { navController.navigate(Screen.Checkout.route) }
            )
        }
    }
}
