package com.example.expensetracker.android.feature.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.android.network.ExpenseApiService
import com.example.expensetracker.android.network.InsightResponse
import com.example.expensetracker.android.network.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InsightsUiState(
    val isLoading: Boolean = false,
    val insight: InsightResponse? = null,
    val error: String? = null
)

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val apiService: ExpenseApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init { loadInsights() }

    fun loadInsights() {
        viewModelScope.launch {
            _uiState.value = InsightsUiState(isLoading = true)
            try {
                val response = apiService.getInsights(tokenManager.getBearerToken())
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = InsightsUiState(insight = response.body())
                } else {
                    _uiState.value = InsightsUiState(error = "Could not load insights")
                }
            } catch (e: Exception) {
                _uiState.value = InsightsUiState(error = "Connection error")
            }
        }
    }
}