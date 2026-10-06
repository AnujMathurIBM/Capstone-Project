package com.bookworm.app.feature.landing.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bookworm.app.core.UiState
import com.bookworm.app.data.remote.TestApiServiceImpl
import com.bookworm.app.data.remote.dto.CategoryDto
import com.bookworm.app.data.remote.dto.ProductSummaryDto
import com.bookworm.app.feature.landing.viewmodel.LandingViewModel
import com.bookworm.app.ui.components.ProductCard
import com.bookworm.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    onCategorySelected: (String) -> Unit,
    onProductClicked: (String) -> Unit,
    onCartClicked: () -> Unit,
    onOrderHistoryClicked: () -> Unit,
    viewModel: LandingViewModel = hiltViewModel()
) {
    val categories  by viewModel.categories.collectAsStateWithLifecycle()
    val recommended by viewModel.recommended.collectAsStateWithLifecycle()
    val bestsellers by viewModel.bestsellers.collectAsStateWithLifecycle()
    val newLaunches by viewModel.newLaunches.collectAsStateWithLifecycle()
    val cartCount   by viewModel.cartItemCount.collectAsStateWithLifecycle()

    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Drawer state — starts closed
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = SurfaceDark,
                modifier = Modifier.width(240.dp)
            ) {
                // Drawer header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDarker)
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "Categories",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                }

                HorizontalDivider(color = TextMuted)

                // "All" item
                DrawerCategoryItem(
                    name = "All",
                    isSelected = selectedCategoryId == null,
                    onClick = {
                        selectedCategoryId = null
                        scope.launch { drawerState.close() }
                    }
                )

                // Category items
                if (categories is UiState.Success) {
                    (categories as UiState.Success<List<CategoryDto>>).data
                        .filter { it.slug != "all" }
                        .forEach { cat ->
                            DrawerCategoryItem(
                                name = cat.name,
                                isSelected = selectedCategoryId == cat.categoryId,
                                onClick = {
                                    selectedCategoryId = cat.categoryId
                                    scope.launch { drawerState.close() }
                                    onCategorySelected(cat.categoryId)
                                }
                            )
                        }
                }
            }
        },
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Book Worm",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        // Hamburger — opens the category drawer
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Categories", tint = TextPrimary)
                        }
                    },
                    actions = {
                        TextButton(onClick = onOrderHistoryClicked) {
                            Text("My Orders", color = TextSecondary, fontSize = 13.sp)
                        }
                        TextButton(onClick = { /* wishlist */ }) {
                            Text("My Wishlist", color = TextSecondary, fontSize = 13.sp)
                        }
                        TextButton(onClick = { /* writers */ }) {
                            Text("My Writers", color = TextSecondary, fontSize = 13.sp)
                        }
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) Badge { Text(cartCount.toString()) }
                            },
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            IconButton(onClick = onCartClicked) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = TextPrimary)
                            }
                        }
                        IconButton(onClick = { /* profile */ }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = TextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
                )
            },
            containerColor = BackgroundDark
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Search bar
                item {
                    SearchFilterBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it }
                    )
                }

                // Active category chip (shown when a category is selected)
                if (selectedCategoryId != null) {
                    val selectedName = (categories as? UiState.Success)
                        ?.data?.firstOrNull { it.categoryId == selectedCategoryId }?.name
                    if (selectedName != null) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Browsing:",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                                Surface(
                                    color = AccentBlue.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(selectedName, color = AccentBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            "✕",
                                            color = AccentBlue,
                                            fontSize = 12.sp,
                                            modifier = Modifier.clickable { selectedCategoryId = null }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Recommended for You
                item {
                    ProductSection(
                        title = "Recommended for You",
                        state = recommended,
                        onProductClicked = onProductClicked
                    )
                }

                // Bestsellers this Month
                item {
                    ProductSection(
                        title = "Bestsellers this Month",
                        state = bestsellers,
                        onProductClicked = onProductClicked
                    )
                }

                // New Launches
                item {
                    ProductSection(
                        title = "New Launches",
                        state = newLaunches,
                        onProductClicked = onProductClicked
                    )
                }
            }
        }
    }
}

// ─── Drawer Category Item ─────────────────────────────────────────────────────

@Composable
private fun DrawerCategoryItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = name,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isSelected) SidebarSelected else SurfaceDark)
            .padding(horizontal = 20.dp, vertical = 13.dp),
        color = if (isSelected) TextPrimary else TextSecondary,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
        fontSize = 14.sp
    )
}

// ─── Category Side Rail (kept for CatalogScreen reuse) ───────────────────────

@Composable
fun CategorySideRail(
    categoriesState: UiState<List<CategoryDto>>,
    selectedId: String?,
    onCategoryClick: (CategoryDto?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            CategoryRailItem(
                name = "All",
                isSelected = selectedId == null,
                onClick = { onCategoryClick(null) }
            )
        }
        if (categoriesState is UiState.Success) {
            items(categoriesState.data) { cat ->
                CategoryRailItem(
                    name = cat.name,
                    isSelected = selectedId == cat.categoryId,
                    onClick = { onCategoryClick(cat) }
                )
            }
        }
    }
}

@Composable
fun CategoryRailItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = name,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isSelected) SidebarSelected else SurfaceDark)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        color = if (isSelected) TextPrimary else TextSecondary,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
        fontSize = 13.sp
    )
}

// ─── Search + Filter Bar ──────────────────────────────────────────────────────

@Composable
fun SearchFilterBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search you want to read here", fontSize = 12.sp, color = TextMuted) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AccentBlue,
            unfocusedBorderColor = TextMuted,
            focusedTextColor     = TextPrimary,
            unfocusedTextColor   = TextPrimary
        )
    )
}

// ─── Product Section ──────────────────────────────────────────────────────────

@Composable
fun ProductSection(
    title: String,
    state: UiState<List<ProductSummaryDto>>,
    onProductClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        when (state) {
            is UiState.Loading -> {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentBlue)
                }
            }
            is UiState.Success -> {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.data) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClicked(product.productId) }
                        )
                    }
                }
            }
            is UiState.Error -> {
                Text(
                    text = "Failed to load. Tap to retry.",
                    color = Error,
                    fontSize = 13.sp
                )
            }
            else -> {}
        }
    }
}

@Preview
@Composable
fun LandingScreenPreview() {
    BookWormTheme {
        LandingScreen(
            onCategorySelected = {},
            onProductClicked = {},
            onCartClicked = {},
            onOrderHistoryClicked = {},
            viewModel = LandingViewModel(TestApiServiceImpl())
        )
    }
}
