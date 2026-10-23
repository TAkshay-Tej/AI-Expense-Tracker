package com.example.expensetracker.android.di


import com.example.expensetracker.android.network.ExpenseApiService
import com.example.expensetracker.android.network.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideExpenseApiService(): ExpenseApiService {
        return RetrofitClient.create()
    }
}