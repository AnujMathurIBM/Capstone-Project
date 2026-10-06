package com.bookworm.app.feature.product.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.bookworm.app.core.UiState
import com.bookworm.app.data.remote.dto.PagedResponse
import com.bookworm.app.data.remote.dto.ProductDto
import com.bookworm.app.data.remote.dto.ProductSummaryDto
import com.bookworm.app.data.remote.dto.ReviewDto
import com.bookworm.app.feature.product.viewmodel.ProductDetailViewModel
import com.bookworm.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClicked: () -> Unit,
    onGoToCart: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val productState    by viewModel.product.collectAsStateWithLifecycle()
    val relatedState    by viewModel.related.collectAsStateWithLifecycle()
    val reviewsState    by viewModel.reviews.collectAsStateWithLifecycle()
    val cartActionState by viewModel.cartAction.collectAsStateWithLifecycle()
    val snackbarHost    = remember { SnackbarHostState() }

    LaunchedEffect(productId) { viewModel.loadProduct(productId) }

    LaunchedEffect(cartActionState) {
        when (cartActionState) {
            is UiState.Success -> {
                snackbarHost.showSnackbar("Added to cart!")
                viewModel.resetCartAction()
            }
            is UiState.Error -> {
                snackbarHost.showSnackbar((cartActionState as UiState.Error).message)
                viewModel.resetCartAction()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onGoToCart) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        when (val state = productState) {
            is UiState.Loading -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentBlue)
                }
            }

            is UiState.Success -> {
                val product = state.data
                val related = (relatedState as? UiState.Success<List<ProductSummaryDto>>)?.data
                    ?: emptyList()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {

                    // ── 1. Breadcrumb ─────────────────────────────────────────
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceDark)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Home", color = TextLink, fontSize = 12.sp)
                            Text(" / ", color = TextMuted, fontSize = 12.sp)
                            Text(product.category.name, color = TextLink, fontSize = 12.sp)
                            Text(" / ", color = TextMuted, fontSize = 12.sp)
                            Text(
                                product.title,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                    }

                    // ── 2. Hero cover image ───────────────────────────────────
                    item {
                        val coverUrl = product.images.firstOrNull { it.isCover }?.url
                            ?: product.images.firstOrNull()?.url

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        ) {
                            AsyncImage(
                                model = coverUrl,
                                contentDescription = product.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Gradient fade at bottom
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, BackgroundDark)
                                        )
                                    )
                            )
                        }
                    }

                    // ── 3. Title, author, publisher, tags ─────────────────────
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BackgroundDark)
                                .padding(horizontal = 16.dp)
                                .padding(top = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Title
                            Text(
                                text = product.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = TextPrimary,
                                lineHeight = 28.sp
                            )
                            // Author
                            Text(
                                text = "by ${product.brand?.name ?: "Unknown"}",
                                color = TextLink,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            // Publisher
                            product.publisher?.let {
                                Text(
                                    text = "Published by $it",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                            // Format + tags chips
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                TagChip(
                                    label = product.format.lowercase()
                                        .replaceFirstChar { it.uppercase() }
                                )
                                product.tags.take(2).forEach { TagChip(label = it) }
                            }
                        }
                    }

                    // ── 4. Price + delivery + action buttons ──────────────────
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BackgroundDark)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Price row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "₹${product.salePrice ?: product.price}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 26.sp,
                                    color = TextPrimary
                                )
                                if (product.salePrice != null) {
                                    Text(
                                        text = "₹${product.price}",
                                        fontSize = 15.sp,
                                        color = TextMuted,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                        )
                                    )
                                }
                            }
                            // Delivery
                            product.deliveryDate?.let {
                                Text(
                                    text = "Delivery by $it",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                            // Action buttons — full width, stacked
                            Button(
                                onClick = { viewModel.addToCart(productId) },
                                enabled = cartActionState !is UiState.Loading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                            ) {
                                if (cartActionState is UiState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = TextPrimary
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Add to Cart",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                            OutlinedButton(
                                onClick = { viewModel.addToWishlist(productId) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = TextSecondary
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    width = 1.dp
                                )
                            ) {
                                Icon(
                                    Icons.Default.BookmarkBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = TextSecondary
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Add to Wishlist",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    // ── 5. Metadata chips (Language · Rating · Sells) ─────────
                    item {
                        HorizontalDivider(
                            color = TextMuted.copy(alpha = 0.3f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MetaChip(
                                icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, null, modifier = Modifier.size(15.dp), tint = TextMuted) },
                                label = "Language",
                                value = product.language
                            )
                            VerticalDivider(
                                modifier = Modifier.height(36.dp),
                                color = TextMuted.copy(alpha = 0.3f)
                            )
                            MetaChip(
                                icon = {
                                    Icon(Icons.Default.Star, null, modifier = Modifier.size(15.dp), tint = Warning)
                                },
                                label = "Rating",
                                value = "%.1f / 5.0".format(product.ratingAvg)
                            )
                            VerticalDivider(
                                modifier = Modifier.height(36.dp),
                                color = TextMuted.copy(alpha = 0.3f)
                            )
                            MetaChip(
                                icon = null,
                                label = "Sells",
                                value = "${product.sellCount} sold"
                            )
                        }
                        HorizontalDivider(
                            color = TextMuted.copy(alpha = 0.3f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    // ── 6. Description ────────────────────────────────────────
                    item {
                        var expanded by remember { mutableStateOf(false) }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "About this book",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = product.description ?: "No description available.",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                maxLines = if (expanded) Int.MAX_VALUE else 4,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (expanded) "Show less" else "Read more",
                                color = AccentBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable { expanded = !expanded }
                            )
                        }
                    }

                    // ── 7. Related Reads (horizontal scroll) ──────────────────
                    if (related.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceDark)
                                    .padding(vertical = 16.dp)
                            ) {
                                Text(
                                    "Related Reads",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(related) { relProduct ->
                                        RelatedReadCard(
                                            product = relProduct,
                                            onClick = { viewModel.loadProduct(relProduct.productId) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── 8. About the writer ───────────────────────────────────
                    if (product.authorBio != null) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    "About the writer",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                HorizontalDivider(color = TextMuted.copy(alpha = 0.3f))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Author avatar initials
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(AccentBlue.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = product.brand?.name
                                                ?.split(" ")
                                                ?.take(2)
                                                ?.mapNotNull { it.firstOrNull()?.toString() }
                                                ?.joinToString("") ?: "A",
                                            color = AccentBlue,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            product.brand?.name ?: "",
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            product.authorBio,
                                            color = TextSecondary,
                                            fontSize = 13.sp,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── 9. Reviews ────────────────────────────────────────────
                    item {
                        ReviewsSection(
                            reviewsState = reviewsState,
                            onSubmitReview = { rating, body ->
                                viewModel.submitReview(productId, rating, body)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }

                    // Bottom spacing
                    item { Spacer(Modifier.height(32.dp)) }
                }
            }

            is UiState.Error -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = Error, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }

            else -> {}
        }
    }
}

// ─── Tag chip ─────────────────────────────────────────────────────────────────

@Composable
private fun TagChip(label: String) {
    Surface(
        color = AccentBlue.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = label,
            color = AccentBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// ─── Metadata chip ────────────────────────────────────────────────────────────

@Composable
private fun MetaChip(
    icon: (@Composable () -> Unit)?,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon?.invoke()
            Text(label, color = TextMuted, fontSize = 11.sp)
        }
        Text(value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Related Read card (compact horizontal) ────────────────────────────────────

@Composable
private fun RelatedReadCard(
    product: ProductSummaryDto,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .width(260.dp)
            .background(CardDark, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Cover thumbnail
        AsyncImage(
            model = product.coverImageUrl,
            contentDescription = product.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(64.dp)
                .height(88.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceDark)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                product.title,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "by ${product.authorName}",
                color = TextLink,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                product.format.lowercase().replaceFirstChar { it.uppercase() },
                color = TextMuted,
                fontSize = 11.sp
            )
            if (product.tags.isNotEmpty()) {
                Text(
                    product.tags.take(2).joinToString(", "),
                    color = AccentBlue,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
            Text(
                "₹${product.salePrice?.takeIf { it.isNotBlank() } ?: product.price}",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 14.sp
            )
            product.deliveryDate?.let {
                Text("Delivery by $it", color = TextMuted, fontSize = 10.sp)
            }
        }
    }
}

// ─── Reviews section ──────────────────────────────────────────────────────────

@Composable
private fun ReviewsSection(
    reviewsState: UiState<*>,
    onSubmitReview: (rating: Int, body: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var reviewBody  by remember { mutableStateOf("") }
    var starRating  by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Reviews",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = TextPrimary
        )
        HorizontalDivider(color = TextMuted.copy(alpha = 0.3f))

        // Leave a review form
        Text("Leave Your Review", color = TextSecondary, fontSize = 13.sp)
        OutlinedTextField(
            value = reviewBody,
            onValueChange = { if (it.length <= 100) reviewBody = it },
            placeholder = { Text("Share your thoughts…", color = TextMuted, fontSize = 12.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AccentBlue,
                unfocusedBorderColor = TextMuted,
                focusedTextColor     = TextPrimary,
                unfocusedTextColor   = TextPrimary
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Star rating picker
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                (1..5).forEach { star ->
                    IconButton(
                        onClick = { starRating = star },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (star <= starRating) Icons.Default.Star
                                          else Icons.Outlined.StarOutline,
                            contentDescription = "$star stars",
                            tint = if (star <= starRating) Warning else TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
            Text(
                "${reviewBody.length}/100",
                color = TextMuted,
                fontSize = 11.sp
            )
        }
        Button(
            onClick = { onSubmitReview(starRating, reviewBody.ifBlank { null }) },
            enabled = starRating > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text("Submit Review", fontWeight = FontWeight.SemiBold)
        }

        // Existing reviews
        if (reviewsState is UiState.Success<*>) {
            @Suppress("UNCHECKED_CAST")
            val reviews =
                (reviewsState as UiState.Success<PagedResponse<ReviewDto>>).data.content
            if (reviews.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                reviews.forEach { review ->
                    ReviewCard(review)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

// ─── Single review card ───────────────────────────────────────────────────────

@Composable
private fun ReviewCard(review: ReviewDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(10.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${review.user.firstName} ${review.user.lastName}",
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                fontSize = 14.sp
            )
            // Star display
            Row {
                repeat(review.rating) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Warning,
                        modifier = Modifier.size(14.dp)
                    )
                }
                repeat(5 - review.rating) {
                    Icon(
                        Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        review.body?.let {
            Text(
                it,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
        }
        Text(
            review.createdAt.take(10),
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}
