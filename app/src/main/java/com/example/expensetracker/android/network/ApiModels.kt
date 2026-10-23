package com.example.expensetracker.android.network


data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AddExpenseRequest(
    val title: String,
    val amount: Double,
    val type: String,       // "EXPENSE" or "INCOME"
    val notes: String? = null,
    val category: String? = null  // null = AI auto-categorizes
)

// ── Response models ────────────────────────────────────────

data class AuthResponse(
    val token: String,
    val name: String,
    val email: String
)

data class ExpenseResponse(
    val id: Long,
    val title: String,
    val amount: Double,
    val category: String,
    val type: String,
    val notes: String?,
    val transactionDate: String?
)

data class InsightResponse(
    val summary: String,
    val topSpendingCategory: String,
    val advice: String,
    val totalExpenses: Double,
    val totalIncome: Double
)