package com.bookworm.app.feature.orders.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bookworm.app.core.UiState
import com.bookworm.app.data.remote.dto.OrderSummaryDto
import com.bookworm.app.feature.orders.viewmodel.OrderViewModel
import com.bookworm.app.ui.theme.*
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    onBackClicked: () -> Unit,
    onBuyAgain: () -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val historyState   by viewModel.orderHistory.collectAsStateWithLifecycle()
    val cancelState    by viewModel.cancelState.collectAsStateWithLifecycle()
    val buyAgainState  by viewModel.buyAgainState.collectAsStateWithLifecycle()
    val snackbarHost   = remember { SnackbarHostState() }

    var cancelDialogOrder by remember { mutableStateOf<OrderSummaryDto?>(null) }

    LaunchedEffect(Unit) { viewModel.loadOrderHistory() }

    LaunchedEffect(cancelState) {
        if (cancelState is UiState.Success) { snackbarHost.showSnackbar("Order cancelled successfully."); viewModel.resetCancelState() }
        if (cancelState is UiState.Error)   { snackbarHost.showSnackbar((cancelState as UiState.Error).message); viewModel.resetCancelState() }
    }

    LaunchedEffect(buyAgainState) {
        if (buyAgainState is UiState.Success) { snackbarHost.showSnackbar("Items added to cart!"); viewModel.resetBuyAgainState(); onBuyAgain() }
        if (buyAgainState is UiState.Error)   { snackbarHost.showSnackbar((buyAgainState as UiState.Error).message); viewModel.resetBuyAgainState() }
    }

    // Cancel confirmation dialog
    cancelDialogOrder?.let { order ->
        AlertDialog(
            onDismissRequest = { cancelDialogOrder = null },
            title = { Text("Cancel Order", color = TextPrimary) },
            text  = { Text("Are you sure you want to cancel order #${order.orderId.take(8).uppercase()}?", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { viewModel.cancelOrder(order.orderId); cancelDialogOrder = null }) {
                    Text("Yes, Cancel", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { cancelDialogOrder = null }) { Text("No", color = TextSecondary) }
            },
            containerColor = CardDark
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            TopAppBar(
                title = { Text("My Orders", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        when (val state = historyState) {
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentBlue)
                }
            }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Text("No orders yet.", color = TextSecondary)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(state.data) { order ->
                            OrderCard(
                                order         = order,
                                onBuyAgain    = { viewModel.buyAgain(order.orderId) },
                                onCancelClick = { cancelDialogOrder = order }
                            )
                        }
                    }
                }
            }
            is UiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(state.message, color = Error)
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun OrderCard(
    order: OrderSummaryDto,
    onBuyAgain: () -> Unit,
    onCancelClick: () -> Unit
) {
    val isCancellable = remember(order) {
        try {
            val deadline = OffsetDateTime.parse(order.cancelDeadline ?: return@remember false)
            OffsetDateTime.now().isBefore(deadline)
        } catch (_: Exception) { false }
    }
    val statusColor = when (order.status) {
        "CONFIRMED"  -> Success
        "SHIPPED"    -> AccentBlue
        "DELIVERED"  -> Success
        "CANCELLED"  -> Error
        "RETURNED"   -> Warning
        else         -> TextMuted
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Order #${order.orderId.take(8).uppercase()}", fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Surface(
                color = statusColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    order.status,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Text("${order.itemCount} item(s) · ₹${order.payableAmount}", color = TextSecondary, fontSize = 13.sp)
        Text(order.createdAt.take(10), color = TextMuted, fontSize = 11.sp)

        HorizontalDivider(color = SurfaceDark)

        // Action buttons
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onBuyAgain,
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Buy Again", fontSize = 13.sp)
            }
            if (isCancellable) {
                OutlinedButton(
                    onClick = onCancelClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Error)
                ) {
                    Text("Cancel Order", fontSize = 13.sp, color = Error)
                }
            }
        }
    }
}
