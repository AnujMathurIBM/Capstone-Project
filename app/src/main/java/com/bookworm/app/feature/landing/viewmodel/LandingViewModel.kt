package com.bookworm.app.feature.landing.viewmodel

import androidx.lifecycle.viewModelScope
import com.bookworm.app.core.BaseViewModel
import com.bookworm.app.core.UiState
import com.bookworm.app.data.remote.ApiService
import com.bookworm.app.data.remote.dto.CategoryDto
import com.bookworm.app.data.remote.dto.ProductSummaryDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val api: ApiService
) : BaseViewModel() {

    private val _categories  = MutableStateFlow<UiState<List<CategoryDto>>>(UiState.Idle)
    val categories: StateFlow<UiState<List<CategoryDto>>> = _categories

    private val _recommended = MutableStateFlow<UiState<List<ProductSummaryDto>>>(UiState.Idle)
    val recommended: StateFlow<UiState<List<ProductSummaryDto>>> = _recommended

    private val _bestsellers = MutableStateFlow<UiState<List<ProductSummaryDto>>>(UiState.Idle)
    val bestsellers: StateFlow<UiState<List<ProductSummaryDto>>> = _bestsellers

    private val _newLaunches = MutableStateFlow<UiState<List<ProductSummaryDto>>>(UiState.Idle)
    val newLaunches: StateFlow<UiState<List<ProductSummaryDto>>> = _newLaunches

    val cartItemCount = MutableStateFlow(0)

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _categories.value  = UiState.Loading
            _recommended.value = UiState.Loading
            _bestsellers.value = UiState.Loading
            _newLaunches.value = UiState.Loading

            val catDef  = async { api.getCategories() }
            val recDef  = async { api.getRecommended() }
            val bestDef = async { api.getBestsellers() }
            val newDef  = async { api.getNewLaunches() }

            catDef.await().let  { r -> _categories.value  = if (r.isSuccessful) UiState.Success(r.body()!!) else UiState.Error("Failed to load categories") }
            recDef.await().let  { r -> _recommended.value = if (r.isSuccessful) UiState.Success(r.body()!!) else UiState.Error("Failed to load recommendations") }
            bestDef.await().let { r -> _bestsellers.value = if (r.isSuccessful) UiState.Success(r.body()!!) else UiState.Error("Failed to load bestsellers") }
            newDef.await().let  { r -> _newLaunches.value = if (r.isSuccessful) UiState.Success(r.body()!!) else UiState.Error("Failed to load new launches") }
        }
    }
}
