package com.example.expensetracker.android.network

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

// ── Token Manager ──────────────────────────────────────────────────────────────
// Stores JWT token in SharedPreferences so it persists across app restarts.

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("expense_tracker_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) = prefs.edit().putString(KEY_TOKEN, token).apply()
    fun saveUser(name: String, email: String) {
        prefs.edit().putString(KEY_NAME, name).putString(KEY_EMAIL, email).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun getUserName(): String? = prefs.getString(KEY_NAME, null)
    fun getUserEmail(): String? = prefs.getString(KEY_EMAIL, null)
    fun getBearerToken(): String = "Bearer ${getToken()}"
    fun isLoggedIn(): Boolean = getToken() != null

    fun clear() = prefs.edit().clear().apply()

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_NAME  = "user_name"
        private const val KEY_EMAIL = "user_email"
    }
}

// ── Retrofit Builder ───────────────────────────────────────────────────────────

object RetrofitClient {

    private const val BASE_URL = "" //backend URL

    fun create(): ExpenseApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExpenseApiService::class.java)
    }
}