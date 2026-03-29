package com.example.expensetracker.android.feature.stats

import androidx.lifecycle.viewModelScope
import com.example.expensetracker.android.base.BaseViewModel
import com.example.expensetracker.android.base.UiEvent
import com.example.expensetracker.android.network.ExpenseApiService
import com.example.expensetracker.android.network.ExpenseResponse
import com.example.expensetracker.android.network.TokenManager
import com.example.expensetracker.android.utils.Utils
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatsUiState(
    val expenses: List<ExpenseResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val apiService: ExpenseApiService,
    private val tokenManager: TokenManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init { loadExpenses() }

    private fun loadExpenses() {
        viewModelScope.launch {
            _uiState.value = StatsUiState(isLoading = true)
            try {
                val response = apiService.getExpenses(tokenManager.getBearerToken())
                _uiState.value = if (response.isSuccessful) {
                    StatsUiState(expenses = response.body() ?: emptyList())
                } else {
                    StatsUiState(error = "Failed to load stats")
                }
            } catch (e: Exception) {
                _uiState.value = StatsUiState(error = "Connection error")
            }
        }
    }

    fun getEntriesForChart(expenses: List<ExpenseResponse>): List<Entry> {
        return expenses
            .filter { it.type == "EXPENSE" }
            .groupBy { it.transactionDate?.substring(0, 10) ?: "" }
            .mapNotNull { (date, items) ->
                val parts = date.split("-")
                val reformatted = if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else date
                val millis = Utils.getMillisFromDate(reformatted)
                val total = items.sumOf { it.amount }.toFloat()
                Entry(millis.toFloat(), total)
            }
            .sortedBy { it.x }
    }

    fun getTopExpenses(expenses: List<ExpenseResponse>): List<ExpenseResponse> =
        expenses.filter { it.type == "EXPENSE" }.sortedByDescending { it.amount }.take(5)

    override fun onEvent(event: UiEvent) {}
}