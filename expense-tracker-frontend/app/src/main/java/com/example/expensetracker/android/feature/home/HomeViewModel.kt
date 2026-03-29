package com.example.expensetracker.android.feature.home


import androidx.lifecycle.viewModelScope
import com.example.expensetracker.android.base.BaseViewModel
import com.example.expensetracker.android.base.HomeNavigationEvent
import com.example.expensetracker.android.base.UiEvent
import com.example.expensetracker.android.network.ExpenseApiService
import com.example.expensetracker.android.network.ExpenseResponse
import com.example.expensetracker.android.network.TokenManager
import com.example.expensetracker.android.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val expenses: List<ExpenseResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val userName: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ExpenseApiService,
    private val tokenManager: TokenManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadExpenses()
        _uiState.value = _uiState.value.copy(
            userName = tokenManager.getUserName() ?: "User"
        )
    }

    fun loadExpenses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = apiService.getExpenses(tokenManager.getBearerToken())
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        expenses = response.body() ?: emptyList(),
                        isLoading = false
                    )
                } else if (response.code() == 403) {
                    // Token expired — navigate to login
                    tokenManager.clear()
                    _navigationEvent.emit(HomeNavigationEvent.NavigateToLogin)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, error = "Failed to load expenses"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, error = "Connection error"
                )
            }
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            try {
                apiService.deleteExpense(tokenManager.getBearerToken(), id)
                loadExpenses() // Refresh list
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Could not delete expense")
            }
        }
    }

    fun logout() {
        tokenManager.clear()
        viewModelScope.launch {
            _navigationEvent.emit(HomeNavigationEvent.NavigateToLogin)
        }
    }

    fun getBalance(list: List<ExpenseResponse>): String {
        val balance = list.sumOf { if (it.type == "INCOME") it.amount else -it.amount }
        return Utils.formatCurrency(balance)
    }

    fun getTotalExpense(list: List<ExpenseResponse>): String {
        val total = list.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        return Utils.formatCurrency(total)
    }

    fun getTotalIncome(list: List<ExpenseResponse>): String {
        val total = list.filter { it.type == "INCOME" }.sumOf { it.amount }
        return Utils.formatCurrency(total)
    }

    override fun onEvent(event: UiEvent) {
        when (event) {
            is HomeUiEvent.OnAddExpenseClicked ->
                viewModelScope.launch { _navigationEvent.emit(HomeNavigationEvent.NavigateToAddExpense) }
            is HomeUiEvent.OnAddIncomeClicked ->
                viewModelScope.launch { _navigationEvent.emit(HomeNavigationEvent.NavigateToAddIncome) }
            is HomeUiEvent.OnSeeAllClicked ->
                viewModelScope.launch { _navigationEvent.emit(HomeNavigationEvent.NavigateToSeeAll) }
        }
    }
}

sealed class HomeUiEvent : UiEvent() {
    object OnAddExpenseClicked : HomeUiEvent()
    object OnAddIncomeClicked : HomeUiEvent()
    object OnSeeAllClicked : HomeUiEvent()
}
