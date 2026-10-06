package com.bookworm.app.feature.orders.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.bookworm.app.core.UiState
import com.bookworm.app.data.remote.dto.OrderDto
import com.bookworm.app.feature.orders.viewmodel.OrderViewModel
import com.bookworm.app.ui.theme.*

@Composable
fun OrderSuccessScreen(
    orderId: String,
    onContinueShopping: () -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val orderState by viewModel.order.collectAsStateWithLifecycle()

    LaunchedEffect(orderId) { viewModel.loadOrder(orderId) }

    // Animated checkmark scale
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "checkmarkScale"
    )

    Scaffold(containerColor = BackgroundDark) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CardDarker),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.85f).wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Green animated checkmark
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(Success),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(36.dp))
                    }

                    Text(
                        text = "Your purchase of the\nfollowing reads is successful",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )

                    // Purchased items
                    when (val state = orderState) {
                        is UiState.Loading -> CircularProgressIndicator(color = AccentBlue)
                        is UiState.Success -> {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                state.data.items.take(3).forEach { item ->
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        AsyncImage(
                                            model = item.product.coverImageUrl,
                                            contentDescription = item.product.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(140.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(SurfaceDark)
                                        )
                                        Text(item.product.title, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 13.sp, maxLines = 2, textAlign = TextAlign.Center)
                                        Text("by ${item.product.authorName}", color = TextLink, fontSize = 11.sp)
                                        Text(item.product.format.lowercase().replaceFirstChar { it.uppercase() }, color = TextMuted, fontSize = 11.sp)
                                        Text(item.product.tags.take(2).joinToString(", "), color = AccentBlue, fontSize = 10.sp)
                                        Text("₹${item.unitPrice}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                        item.product.deliveryDate?.let { Text("Delivery by $it", color = TextMuted, fontSize = 10.sp) }
                                    }
                                }
                            }
                        }
                        else -> {}
                    }

                    Button(
                        onClick = onContinueShopping,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Continue your Shopping", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
