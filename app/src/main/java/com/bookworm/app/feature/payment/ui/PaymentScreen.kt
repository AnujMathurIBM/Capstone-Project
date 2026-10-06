package com.bookworm.app.feature.payment.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bookworm.app.core.UiState
import com.bookworm.app.data.remote.TestApiServiceImpl
import com.bookworm.app.data.remote.dto.PaymentDto
import com.bookworm.app.feature.payment.viewmodel.PaymentMethod
import com.bookworm.app.feature.payment.viewmodel.PaymentViewModel
import com.bookworm.app.ui.theme.*

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    orderId: String,
    onPaymentSuccess: (String) -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val orderState     by viewModel.order.collectAsStateWithLifecycle()
    val paymentState   by viewModel.paymentResult.collectAsStateWithLifecycle()
    val selectedMethod by viewModel.selectedMethod.collectAsStateWithLifecycle()
    val snackbarHost   = remember { SnackbarHostState() }

    LaunchedEffect(orderId) { viewModel.loadOrder(orderId) }

    LaunchedEffect(paymentState) {
        when (paymentState) {
            is UiState.Success -> {
                val payment = (paymentState as UiState.Success<PaymentDto>).data
                viewModel.resetPaymentResult()
                onPaymentSuccess(payment.orderId)
            }
            is UiState.Error -> {
                snackbarHost.showSnackbar((paymentState as UiState.Error).message)
                viewModel.resetPaymentResult()
            }
            else -> {}
        }
    }

    val payableAmount  = (orderState as? UiState.Success)?.data?.payableAmount ?: "—"
    val isLoading      = paymentState is UiState.Loading

    // Form state
    var cardNumber by remember { mutableStateOf("") }
    var nameOnCard by remember { mutableStateOf("") }
    var cvv        by remember { mutableStateOf("") }
    var expiry     by remember { mutableStateOf("") }
    var upiId      by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Complete Payment",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 17.sp
                        )
                        Text(
                            "Secure & encrypted",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* back handled by nav */ }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    // Payable amount chip
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AccentBlue.copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(
                            text = "₹$payableAmount",
                            color = AccentBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        // ── Sticky Pay button at the bottom ───────────────────────────────────
        bottomBar = {
            PayBottomBar(
                payableAmount = payableAmount,
                isLoading     = isLoading,
                onPay = {
                    viewModel.pay(
                        orderId    = orderId,
                        method     = selectedMethod,
                        cardNumber = cardNumber.replace(" ", "").ifBlank { null },
                        nameOnCard = nameOnCard.ifBlank { null },
                        cvv        = cvv.ifBlank { null },
                        expiryDate = expiry.ifBlank { null },
                        upiId      = upiId.ifBlank { null }
                    )
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ── Method selector tabs ──────────────────────────────────────────
            item {
                MethodTabRow(
                    selected = selectedMethod,
                    onSelect = { viewModel.selectedMethod.value = it }
                )
            }

            // ── Selected method visual card (Credit/Debit only) ───────────────
            if (selectedMethod == PaymentMethod.CREDIT_CARD ||
                selectedMethod == PaymentMethod.DEBIT_CARD) {
                item {
                    Spacer(Modifier.height(20.dp))
                    CardVisual(
                        cardNumber = cardNumber,
                        nameOnCard = nameOnCard,
                        expiry     = expiry,
                        isDebit    = selectedMethod == PaymentMethod.DEBIT_CARD,
                        modifier   = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // ── Form fields ───────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(20.dp))
                when (selectedMethod) {
                    PaymentMethod.CREDIT_CARD,
                    PaymentMethod.DEBIT_CARD -> CardForm(
                        cardNumber       = cardNumber,
                        onCardChange     = { raw ->
                            val digits = raw.filter { it.isDigit() }.take(16)
                            cardNumber = digits.chunked(4).joinToString(" ")
                        },
                        nameOnCard       = nameOnCard,
                        onNameChange     = { nameOnCard = it },
                        cvv              = cvv,
                        onCvvChange      = { if (it.length <= 3 && it.all(Char::isDigit)) cvv = it },
                        expiry           = expiry,
                        onExpiryChange   = { raw ->
                            val digits = raw.filter { it.isDigit() }.take(4)
                            expiry = when {
                                digits.length > 2 -> digits.substring(0, 2) + "/" + digits.substring(2)
                                else -> digits
                            }
                        }
                    )
                    PaymentMethod.UPI    -> UpiForm(upiId, { upiId = it })
                    PaymentMethod.WALLET -> WalletForm()
                }
            }
        }
    }
}

// ─── Method Tab Row ───────────────────────────────────────────────────────────

private data class MethodTab(
    val method: PaymentMethod,
    val label: String,
    val icon: ImageVector
)

private val METHOD_TABS = listOf(
    MethodTab(PaymentMethod.CREDIT_CARD, "Credit Card",  Icons.Default.CreditCard),
    MethodTab(PaymentMethod.DEBIT_CARD,  "Debit Card",   Icons.Default.CreditCard),
    MethodTab(PaymentMethod.UPI,         "UPI",          Icons.Default.Lock),
    MethodTab(PaymentMethod.WALLET,      "Wallet",       Icons.Default.Wallet)
)

@Composable
private fun MethodTabRow(
    selected: PaymentMethod,
    onSelect: (PaymentMethod) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        METHOD_TABS.forEach { tab ->
            val isSelected = tab.method == selected
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onSelect(tab.method) }
                    .border(
                        width = if (isSelected) 1.5.dp else 0.dp,
                        color = if (isSelected) AccentBlue else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    ),
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) AccentBlue.copy(alpha = 0.12f) else CardDark
            ) {
                Column(
                    modifier           = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector        = tab.icon,
                        contentDescription = tab.label,
                        tint               = if (isSelected) AccentBlue else TextMuted,
                        modifier           = Modifier.size(20.dp)
                    )
                    Text(
                        text       = tab.label,
                        color      = if (isSelected) AccentBlue else TextSecondary,
                        fontSize   = 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        textAlign  = TextAlign.Center,
                        maxLines   = 2,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}

// ─── Card Visual ──────────────────────────────────────────────────────────────

@Composable
private fun CardVisual(
    cardNumber: String,
    nameOnCard: String,
    expiry: String,
    isDebit: Boolean,
    modifier: Modifier = Modifier
) {
    val displayNumber = cardNumber
        .padEnd(19, '•')
        .take(19)
        .let {
            // already chunked with spaces — ensure 4-4-4-4 dots
            if (cardNumber.isBlank())
                "•••• •••• •••• ••••"
            else it
        }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = if (isDebit)
                        listOf(Color(0xFF1E3A5F), Color(0xFF0D2137))
                    else
                        listOf(Color(0xFF1A237E), Color(0xFF283593))
                )
            )
            .padding(24.dp)
    ) {
        // Chip
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 32.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFFFD700).copy(alpha = 0.85f))
                .align(Alignment.TopStart)
        )

        // Card type label top-right
        Text(
            text       = if (isDebit) "DEBIT" else "CREDIT",
            color      = TextPrimary.copy(alpha = 0.7f),
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier   = Modifier.align(Alignment.TopEnd)
        )

        // Card number
        Text(
            text       = displayNumber,
            color      = TextPrimary,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier   = Modifier.align(Alignment.Center)
        )

        // Name + expiry row at bottom
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Bottom
        ) {
            Column {
                Text("CARD HOLDER", color = TextMuted, fontSize = 9.sp)
                Text(
                    nameOnCard.ifBlank { "YOUR NAME" }.uppercase(),
                    color      = TextPrimary,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("EXPIRES", color = TextMuted, fontSize = 9.sp)
                Text(
                    expiry.ifBlank { "MM/YY" },
                    color      = TextPrimary,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ─── Card Form ────────────────────────────────────────────────────────────────

@Composable
private fun CardForm(
    cardNumber: String,    onCardChange: (String) -> Unit,
    nameOnCard: String,    onNameChange: (String) -> Unit,
    cvv: String,           onCvvChange: (String) -> Unit,
    expiry: String,        onExpiryChange: (String) -> Unit
) {
    val fieldColors = paymentFieldColors()
    Column(
        modifier            = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Card number — full width
        OutlinedTextField(
            value         = cardNumber,
            onValueChange = onCardChange,
            label         = { Text("Card Number") },
            placeholder   = { Text("XXXX XXXX XXXX XXXX", color = TextMuted) },
            leadingIcon   = {
                Icon(Icons.Default.CreditCard, contentDescription = null, tint = TextMuted)
            },
            modifier        = Modifier.fillMaxWidth(),
            singleLine      = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors          = fieldColors
        )

        // Name on card — full width
        OutlinedTextField(
            value         = nameOnCard,
            onValueChange = onNameChange,
            label         = { Text("Name on Card") },
            placeholder   = { Text("As printed on card", color = TextMuted) },
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = true,
            colors        = fieldColors
        )

        // CVV + Expiry — side by side (only pair that makes sense on mobile)
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value         = cvv,
                onValueChange = onCvvChange,
                label         = { Text("CVV") },
                placeholder   = { Text("•••", color = TextMuted) },
                modifier        = Modifier.weight(1f),
                singleLine      = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon  = {
                    Icon(Icons.Default.Lock, contentDescription = null,
                        tint = TextMuted, modifier = Modifier.size(16.dp))
                },
                colors = fieldColors
            )
            OutlinedTextField(
                value         = expiry,
                onValueChange = onExpiryChange,
                label         = { Text("Expiry") },
                placeholder   = { Text("MM/YY", color = TextMuted) },
                modifier        = Modifier.weight(1f),
                singleLine      = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors          = fieldColors
            )
        }

        // Security note
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier.padding(top = 2.dp)
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                tint     = Success,
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(5.dp))
            Text(
                "Your card details are encrypted and never stored.",
                color    = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

// ─── UPI Form ─────────────────────────────────────────────────────────────────

@Composable
private fun UpiForm(upiId: String, onUpiIdChange: (String) -> Unit) {
    Column(
        modifier            = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value         = upiId,
            onValueChange = onUpiIdChange,
            label         = { Text("UPI ID") },
            placeholder   = { Text("yourname@upi", color = TextMuted) },
            modifier        = Modifier.fillMaxWidth(),
            singleLine      = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors          = paymentFieldColors()
        )

        // UPI app logos row (visual only)
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("GPay", "PhonePe", "Paytm", "BHIM").forEach { app ->
                Surface(
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(8.dp),
                    color    = CardDark
                ) {
                    Text(
                        text      = app,
                        color     = TextSecondary,
                        fontSize  = 10.sp,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lock, null, tint = Success, modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(5.dp))
            Text("Payments via UPI are instant and secure.", color = TextMuted, fontSize = 11.sp)
        }
    }
}

// ─── Wallet Form ──────────────────────────────────────────────────────────────

@Composable
private fun WalletForm() {
    Column(
        modifier            = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Wallet balance card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardDark)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text("BookWorm Wallet", color = TextSecondary, fontSize = 12.sp)
                Text("Available Balance", color = TextMuted, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Icon(
                    Icons.Default.Wallet,
                    contentDescription = null,
                    tint     = AccentBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Text(
            "Your wallet balance will be deducted upon confirmation. " +
            "If the balance is insufficient, the remaining amount can be paid via another method.",
            color    = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lock, null, tint = Success, modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(5.dp))
            Text("Secured and instant wallet deduction.", color = TextMuted, fontSize = 11.sp)
        }
    }
}

// ─── Bottom Pay Bar ───────────────────────────────────────────────────────────

@Composable
private fun PayBottomBar(
    payableAmount: String,
    isLoading: Boolean,
    onPay: () -> Unit
) {
    Surface(
        color       = SurfaceDark,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Summary row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Total Payable", color = TextSecondary, fontSize = 13.sp)
                Text(
                    "₹$payableAmount",
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp
                )
            }

            // Pay button
            Button(
                onClick  = onPay,
                enabled  = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
                    CircularProgressIndicator(
                        Modifier.size(22.dp),
                        strokeWidth = 2.5.dp,
                        color       = Color.White
                    )
                }
                AnimatedVisibility(visible = !isLoading, enter = fadeIn(), exit = fadeOut()) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null,
                            tint = Color.White, modifier = Modifier.size(16.dp))
                        Text("Pay ₹$payableAmount", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            // SSL caption
            Text(
                "🔒 Secured by 256-bit SSL encryption",
                color     = TextMuted,
                fontSize  = 10.sp,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )
        }
    }
}

// ─── Field colours helper ─────────────────────────────────────────────────────

@Composable
private fun paymentFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor       = AccentBlue,
    unfocusedBorderColor     = TextMuted,
    focusedLabelColor        = AccentBlue,
    focusedTextColor         = TextPrimary,
    unfocusedTextColor       = TextPrimary,
    cursorColor              = AccentBlue,
    focusedContainerColor    = CardDark.copy(alpha = 0.5f),
    unfocusedContainerColor  = CardDark.copy(alpha = 0.5f)
)

// ─── PaymentMethod label extension ────────────────────────────────────────────

private fun PaymentMethod.label() = when (this) {
    PaymentMethod.CREDIT_CARD -> "Credit Card"
    PaymentMethod.DEBIT_CARD  -> "Debit Card"
    PaymentMethod.UPI         -> "UPI"
    PaymentMethod.WALLET      -> "Wallet"
}

@Preview
@Composable
fun PaymentScreenPreview() {
    BookWormTheme {
        PaymentScreen(
            orderId = "",
            onPaymentSuccess = {},
            viewModel = PaymentViewModel(TestApiServiceImpl())
        )
    }
}
