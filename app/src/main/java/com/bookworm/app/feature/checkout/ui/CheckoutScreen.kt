package com.bookworm.app.feature.checkout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.bookworm.app.core.UiState
import com.bookworm.app.data.remote.dto.*
import com.bookworm.app.feature.checkout.viewmodel.CheckoutViewModel
import com.bookworm.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBackClicked: () -> Unit,
    onPayNow: (orderId: String) -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val cartState    by viewModel.cart.collectAsStateWithLifecycle()
    val addressState by viewModel.addresses.collectAsStateWithLifecycle()
    val couponState  by viewModel.coupon.collectAsStateWithLifecycle()
    val orderState   by viewModel.order.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    // Form state
    var useSavedAddress by remember { mutableStateOf(false) }
    var selectedAddress by remember { mutableStateOf<AddressDto?>(null) }
    var firstName by remember { mutableStateOf("") }
    var lastName  by remember { mutableStateOf("") }
    var line1     by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var city      by remember { mutableStateOf("") }
    var pin       by remember { mutableStateOf("") }
    var phone     by remember { mutableStateOf("") }
    var state     by remember { mutableStateOf("") }
    var country   by remember { mutableStateOf("India") }
    var couponCode by remember { mutableStateOf("") }

    // When user toggles "Use Saved Address", pre-fill the form
    LaunchedEffect(useSavedAddress, selectedAddress) {
        if (useSavedAddress) {
            val addr = selectedAddress
                ?: (addressState as? UiState.Success)?.data?.firstOrNull { it.isDefault }
                ?: (addressState as? UiState.Success)?.data?.firstOrNull()
            addr?.let {
                firstName = it.firstName; lastName = it.lastName
                line1 = it.line1; email = it.email ?: ""
                city = it.city; pin = it.pin
                phone = it.phone ?: ""; state = it.state
                country = it.country; selectedAddress = it
            }
        }
    }

    // Navigate when order placed
    LaunchedEffect(orderState) {
        when (orderState) {
            is UiState.Success -> {
                val orderId = (orderState as UiState.Success<OrderDto>).data.orderId
                viewModel.resetOrderState()
                onPayNow(orderId)
            }
            is UiState.Error -> {
                snackbarHost.showSnackbar((orderState as UiState.Error).message)
                viewModel.resetOrderState()
            }
            else -> {}
        }
    }

    // Coupon feedback
    LaunchedEffect(couponState) {
        when (couponState) {
            is UiState.Success -> snackbarHost.showSnackbar("Coupon applied! Discount: ₹${(couponState as UiState.Success<CouponValidateResponse>).data.discountAmount}")
            is UiState.Error   -> snackbarHost.showSnackbar((couponState as UiState.Error).message)
            else -> {}
        }
    }

    val cart = (cartState as? UiState.Success)?.data
    val appliedDiscount = (couponState as? UiState.Success)?.data?.discountAmount ?: "0.00"
    val isPlacingOrder  = orderState is UiState.Loading

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            TopAppBar(
                title = { Text("Shopping Cart", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // ── Cart Items ─────────────────────────────────────────────────
            when {
                cartState is UiState.Loading -> {
                    item { Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AccentBlue) } }
                }
                cart != null -> {
                    items(cart.items) { cartItem ->
                        CartItemRow(
                            cartItem = cartItem,
                            onDecrease = { viewModel.updateItemQuantity(cartItem.cartItemId, cartItem.quantity - 1) },
                            onIncrease = { viewModel.updateItemQuantity(cartItem.cartItemId, cartItem.quantity + 1) }
                        )
                    }
                }
            }

            // ── Address section ────────────────────────────────────────────
            item {
                AddressFormSection(
                    modifier = Modifier.fillMaxWidth(),
                    useSaved = useSavedAddress,
                    onUseSavedToggle = { useSavedAddress = it },
                    firstName = firstName, onFirstNameChange = { firstName = it },
                    lastName  = lastName,  onLastNameChange  = { lastName = it },
                    line1     = line1,     onLine1Change     = { line1 = it },
                    email     = email,     onEmailChange     = { email = it },
                    city      = city,      onCityChange      = { city = it },
                    pin       = pin,       onPinChange       = { pin = it },
                    phone     = phone,     onPhoneChange     = { phone = it },
                    state     = state,     onStateChange     = { state = it },
                    country   = country,   onCountryChange   = { country = it }
                )
            }

            // ── Grand Total section ────────────────────────────────────────
            item {
                GrandTotalPanel(
                    modifier = Modifier.fillMaxWidth(),
                    subtotal         = cart?.subtotal ?: "0.00",
                    tax              = "62.00",
                    deliveryCharges  = "Free",
                    discount         = appliedDiscount,
                    couponCode       = couponCode,
                    onCouponChange   = { couponCode = it },
                    onApplyCoupon    = { viewModel.validateCoupon(couponCode, cart?.subtotal ?: "0") },
                    couponLoading    = couponState is UiState.Loading,
                    onPayNow         = {
                        val addrId = selectedAddress?.addressId
                        if (addrId != null) {
                            // Use an already-saved address
                            viewModel.placeOrder(addrId, couponCode.ifBlank { null })
                        } else {
                            // Save the manually-entered address then place the order
                            viewModel.placeOrderWithNewAddress(
                                AddressRequest(
                                    firstName = firstName,
                                    lastName  = lastName,
                                    line1     = line1,
                                    city      = city,
                                    state     = state,
                                    country   = country,
                                    pin       = pin,
                                    phone     = phone.ifBlank { null },
                                    email     = email.ifBlank { null }
                                ),
                                couponCode.ifBlank { null }
                            )
                        }
                    },
                    payNowLoading    = isPlacingOrder,
                    payNowEnabled    = selectedAddress != null || (firstName.isNotBlank() && line1.isNotBlank())
                )
            }
        }
    }
}

// ─── Cart Item Row ────────────────────────────────────────────────────────────

@Composable
fun CartItemRow(
    cartItem: CartItemDto,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    val product = cartItem.product
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(10.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = product.coverImageUrl,
            contentDescription = product.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.width(80.dp).height(110.dp).clip(RoundedCornerShape(6.dp)).background(SurfaceDark)
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(product.title, fontWeight = FontWeight.SemiBold, color = TextPrimary, maxLines = 2)
            Text("by ${product.authorName}", color = TextLink, fontSize = 12.sp)
            Text(product.format.lowercase().replaceFirstChar { it.uppercase() }, color = TextMuted, fontSize = 12.sp)
            Text(product.tags.take(2).joinToString(", "), color = AccentBlue, fontSize = 11.sp)
            Text("₹${product.price}", fontWeight = FontWeight.Bold, color = TextPrimary)
            product.deliveryDate?.let { Text("Delivery by $it", color = TextMuted, fontSize = 11.sp) }
        }
        // Quantity stepper
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("${cartItem.quantity}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Row {
                IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ─── Address Form Section ─────────────────────────────────────────────────────

@Composable
fun AddressFormSection(
    modifier: Modifier = Modifier,
    useSaved: Boolean, onUseSavedToggle: (Boolean) -> Unit,
    firstName: String, onFirstNameChange: (String) -> Unit,
    lastName: String,  onLastNameChange: (String) -> Unit,
    line1: String,     onLine1Change: (String) -> Unit,
    email: String,     onEmailChange: (String) -> Unit,
    city: String,      onCityChange: (String) -> Unit,
    pin: String,       onPinChange: (String) -> Unit,
    phone: String,     onPhoneChange: (String) -> Unit,
    state: String,     onStateChange: (String) -> Unit,
    country: String,   onCountryChange: (String) -> Unit
) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor   = AccentBlue,
        unfocusedBorderColor = TextMuted,
        focusedTextColor     = TextPrimary,
        unfocusedTextColor   = TextPrimary
    )
    Column(
        modifier = modifier.background(CardDark, RoundedCornerShape(10.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Delivery Address", fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 16.sp)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = useSaved,
                onCheckedChange = onUseSavedToggle,
                colors = CheckboxDefaults.colors(checkedColor = AccentBlue)
            )
            Text("Use Saved Address", color = TextSecondary, fontSize = 13.sp)
        }

        // Every field full-width, stacked vertically
        OutlinedTextField(
            value = firstName, onValueChange = onFirstNameChange,
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(), singleLine = true, colors = fieldColors
        )
        OutlinedTextField(
            value = lastName, onValueChange = onLastNameChange,
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(), singleLine = true, colors = fieldColors
        )
        OutlinedTextField(
            value = line1, onValueChange = onLine1Change,
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth(), singleLine = true, colors = fieldColors
        )
        OutlinedTextField(
            value = email, onValueChange = onEmailChange,
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = fieldColors
        )
        OutlinedTextField(
            value = city, onValueChange = onCityChange,
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth(), singleLine = true, colors = fieldColors
        )
        OutlinedTextField(
            value = pin, onValueChange = onPinChange,
            label = { Text("Pin / Postal Code") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = fieldColors
        )
        OutlinedTextField(
            value = phone, onValueChange = onPhoneChange,
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = fieldColors
        )
        OutlinedTextField(
            value = state, onValueChange = onStateChange,
            label = { Text("State") },
            modifier = Modifier.fillMaxWidth(), singleLine = true, colors = fieldColors
        )
        OutlinedTextField(
            value = country, onValueChange = onCountryChange,
            label = { Text("Country") },
            modifier = Modifier.fillMaxWidth(), singleLine = true, colors = fieldColors
        )
    }
}

// ─── Grand Total Panel ────────────────────────────────────────────────────────

@Composable
fun GrandTotalPanel(
    modifier: Modifier = Modifier,
    subtotal: String,
    tax: String,
    deliveryCharges: String,
    discount: String,
    couponCode: String,
    onCouponChange: (String) -> Unit,
    onApplyCoupon: () -> Unit,
    couponLoading: Boolean,
    onPayNow: () -> Unit,
    payNowLoading: Boolean,
    payNowEnabled: Boolean
) {
    Column(
        modifier = modifier.background(CardDark, RoundedCornerShape(10.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Grand Total", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
        TotalRow("Price (${subtotal})", subtotal)
        TotalRow("Tax", tax)
        TotalRow("Delivery Charges", deliveryCharges)

        // Coupon field
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = couponCode,
                onValueChange = onCouponChange,
                placeholder = { Text("Apply Coupon", color = TextMuted, fontSize = 12.sp) },
                modifier = Modifier.weight(1f).height(48.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AccentBlue, unfocusedBorderColor = TextMuted,
                    focusedTextColor     = TextPrimary, unfocusedTextColor  = TextPrimary
                )
            )
            Button(
                onClick = onApplyCoupon,
                enabled = couponCode.isNotBlank() && !couponLoading,
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                if (couponLoading) CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp, color = TextPrimary)
                else Text("Apply", fontSize = 12.sp)
            }
        }

        if (discount != "0.00") TotalRow("Discount", "-₹$discount", valueColor = Success)

        HorizontalDivider(color = TextMuted)
        TotalRow("Total Amount", "₹${computeTotal(subtotal, tax, discount)}", bold = true)

        Button(
            onClick = onPayNow,
            enabled = payNowEnabled && !payNowLoading,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (payNowLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = TextPrimary)
            else Text("Pay Now", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TotalRow(label: String, value: String, bold: Boolean = false, valueColor: androidx.compose.ui.graphics.Color = TextPrimary) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextSecondary, fontSize = 13.sp)
        Text(value, color = valueColor, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
    }
}

private fun computeTotal(subtotal: String, tax: String, discount: String): String {
    return try {
        val total = subtotal.toDouble() + tax.toDouble() - discount.toDouble()
        "%.2f".format(total)
    } catch (_: Exception) { subtotal }
}
