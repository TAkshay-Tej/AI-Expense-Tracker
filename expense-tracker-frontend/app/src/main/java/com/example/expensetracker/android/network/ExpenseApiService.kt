package com.example.expensetracker.android.network

import retrofit2.Response
import retrofit2.http.*

interface ExpenseApiService {

    // ── Auth ──────────────────────────────────────────────

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // ── Expenses ──────────────────────────────────────────

    @GET("api/expenses")
    suspend fun getExpenses(
        @Header("Authorization") token: String
    ): Response<List<ExpenseResponse>>

    @POST("api/expenses")
    suspend fun addExpense(
        @Header("Authorization") token: String,
        @Body request: AddExpenseRequest
    ): Response<ExpenseResponse>

    @DELETE("api/expenses/{id}")
    suspend fun deleteExpense(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Unit>

    // ── AI Insights ───────────────────────────────────────

    @GET("api/expenses/insights")
    suspend fun getInsights(
        @Header("Authorization") token: String
    ): Response<InsightResponse>
}