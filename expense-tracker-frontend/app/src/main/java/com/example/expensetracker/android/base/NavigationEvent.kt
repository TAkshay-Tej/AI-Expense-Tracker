package com.example.expensetracker.android.base

sealed class NavigationEvent {
    object NavigateBack : NavigationEvent()
    object NavigateToHome : NavigationEvent()
}

sealed class AddExpenseNavigationEvent : NavigationEvent() {
    object MenuOpenedClicked : AddExpenseNavigationEvent()
}

sealed class HomeNavigationEvent : NavigationEvent() {
    object NavigateToAddExpense : HomeNavigationEvent()
    object NavigateToAddIncome : HomeNavigationEvent()
    object NavigateToSeeAll : HomeNavigationEvent()
    object NavigateToLogin : HomeNavigationEvent()   // ← new: for logout / token expiry
}