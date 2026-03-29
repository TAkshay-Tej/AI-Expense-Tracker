package com.example.expensetracker.android.feature.add_expense

import androidx.lifecycle.viewModelScope
import com.example.expensetracker.android.base.AddExpenseNavigationEvent
import com.example.expensetracker.android.base.BaseViewModel
import com.example.expensetracker.android.base.NavigationEvent
import com.example.expensetracker.android.base.UiEvent
import com.example.expensetracker.android.network.AddExpenseRequest
import com.example.expensetracker.android.network.ExpenseApiService
import com.example.expensetracker.android.network.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddExpenseUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    // AI-assigned category returned from backend
    val aiCategory: String? = null
)

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val apiService: ExpenseApiService,
    private val tokenManager: TokenManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    override fun onEvent(event: UiEvent) {
        when (event) {
            is AddExpenseUiEvent.OnAddExpenseClicked -> addExpense(event)
            is AddExpenseUiEvent.OnBackPressed -> viewModelScope.launch {
                _navigationEvent.emit(NavigationEvent.NavigateBack)
            }
            is AddExpenseUiEvent.OnMenuClicked -> viewModelScope.launch {
                _navigationEvent.emit(AddExpenseNavigationEvent.MenuOpenedClicked)
            }
        }
    }

    private fun addExpense(event: AddExpenseUiEvent.OnAddExpenseClicked) {
        viewModelScope.launch {
            _uiState.value = AddExpenseUiState(isLoading = true)
            try {
                val request = AddExpenseRequest(
                    title = event.title,
                    amount = event.amount,
                    type = if (event.isIncome) "INCOME" else "EXPENSE",
                    notes = event.notes,
                    category = null  // Let AI auto-categorize
                )
                val response = apiService.addExpense(tokenManager.getBearerToken(), request)
                if (response.isSuccessful) {
                    val saved = response.body()
                    _uiState.value = AddExpenseUiState(
                        success = true,
                        aiCategory = saved?.category
                    )
                    _navigationEvent.emit(NavigationEvent.NavigateBack)
                } else {
                    _uiState.value = AddExpenseUiState(error = "Failed to save. Please try again.")
                }
            } catch (e: Exception) {
                _uiState.value = AddExpenseUiState(error = "Connection error")
            }
        }
    }
}

sealed class AddExpenseUiEvent : UiEvent() {
    data class OnAddExpenseClicked(
        val title: String,
        val amount: Double,
        val isIncome: Boolean,
        val notes: String? = null
    ) : AddExpenseUiEvent()
    object OnBackPressed : AddExpenseUiEvent()
    object OnMenuClicked : AddExpenseUiEvent()
}
