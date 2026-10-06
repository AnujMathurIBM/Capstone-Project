package com.bookworm.app.feature.catalog.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.bookworm.app.feature.catalog.viewmodel.CatalogViewModel
import com.bookworm.app.feature.landing.ui.SearchFilterBar
import com.bookworm.app.ui.components.ProductCard
import com.bookworm.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    categoryId: String?,
    onProductClicked: (String) -> Unit,
    onBackClicked: () -> Unit,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val productsState by viewModel.products.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(categoryId) {
        viewModel.loadProducts(categoryId = categoryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Catalog", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            SearchFilterBar(query = searchQuery, onQueryChange = {
                searchQuery = it
                viewModel.loadProducts(categoryId = categoryId, search = it)
            })

            Spacer(Modifier.height(16.dp))

            when (val state = productsState) {
                is UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentBlue)
                    }
                }
                is UiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement   = Arrangement.spacedBy(16.dp),
                        contentPadding        = PaddingValues(bottom = 24.dp)
                    ) {
                        items(state.data.content) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onProductClicked(product.productId) }
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = Error)
                    }
                }
                else -> {}
            }
        }
    }
}
