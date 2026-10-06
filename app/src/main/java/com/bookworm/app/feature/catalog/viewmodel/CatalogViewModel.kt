package com.bookworm.app.feature.catalog.viewmodel

import androidx.lifecycle.viewModelScope
import com.bookworm.app.core.BaseViewModel
import com.bookworm.app.core.UiState
import com.bookworm.app.data.remote.ApiService
import com.bookworm.app.data.remote.dto.PagedResponse
import com.bookworm.app.data.remote.dto.ProductSummaryDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val api: ApiService
) : BaseViewModel() {

    private val _products = MutableStateFlow<UiState<PagedResponse<ProductSummaryDto>>>(UiState.Idle)
    val products: StateFlow<UiState<PagedResponse<ProductSummaryDto>>> = _products

    var categoryId: String? = null
    private var currentSearch: String? = null
    private var currentFormat: String? = null
    private var currentSort: String?   = "RELEVANCE"

    fun loadProducts(
        categoryId: String? = this.categoryId,
        search: String? = null,
        format: String? = null,
        sort: String? = "RELEVANCE",
        page: Int = 0
    ) {
        this.categoryId = categoryId
        currentSearch   = search
        currentFormat   = format
        currentSort     = sort

        viewModelScope.launch {
            _products.value = UiState.Loading
            try {
                val response = api.getProducts(
                    categoryId = categoryId,
                    search     = search,
                    format     = format,
                    sort       = sort,
                    page       = page
                )
                _products.value = if (response.isSuccessful && response.body() != null) {
                    UiState.Success(response.body()!!)
                } else {
                    UiState.Error("Failed to load products")
                }
            } catch (e: Exception) {
                _products.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }
}
