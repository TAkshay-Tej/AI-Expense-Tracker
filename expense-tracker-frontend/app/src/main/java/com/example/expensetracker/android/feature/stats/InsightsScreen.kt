package com.example.expensetracker.android.feature.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.android.R
import com.example.expensetracker.android.network.InsightResponse
import com.example.expensetracker.android.ui.theme.Typography
import com.example.expensetracker.android.ui.theme.Zinc
import com.example.expensetracker.android.widget.ExpenseTextView

@Composable
fun InsightsScreen(
    navController: NavController,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top bar (reuse app style)
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Zinc)
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterStart).clickable {
                        navController.popBackStack()
                    }
                )
                ExpenseTextView(
                    text = "✨ AI Insights",
                    style = Typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Zinc)
                            Spacer(modifier = Modifier.height(16.dp))
                            ExpenseTextView(text = "Analyzing your spending...", color = Color.Gray)
                        }
                    }
                }
                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                            ExpenseTextView(text = "😕 ${uiState.error}", color = Color.Gray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadInsights() },
                                colors = ButtonDefaults.buttonColors(containerColor = Zinc),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                ExpenseTextView(text = "Try Again", color = Color.White)
                            }
                        }
                    }
                }
                uiState.insight != null -> {
                    InsightsContent(insight = uiState.insight!!)
                }
            }
        }
    }
}

@Composable
fun InsightsContent(insight: InsightResponse) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary & Balance cards
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            InsightStatCard(
                modifier = Modifier.weight(1f),
                label = "Total Spent",
                value = "$%.2f".format(insight.totalExpenses),
                valueColor = Color(0xFFFF4466),
                emoji = "💸"
            )
            InsightStatCard(
                modifier = Modifier.weight(1f),
                label = "Total Income",
                value = "$%.2f".format(insight.totalIncome),
                valueColor = Color(0xFF00CC88),
                emoji = "💰"
            )
        }

        // Top spending category
        InsightCard(
            title = "🏆 Top Spending Category",
            content = insight.topSpendingCategory,
            contentStyle = "large"
        )

        // AI Summary
        InsightCard(
            title = "📊 Spending Summary",
            content = insight.summary
        )

        // AI Advice
        InsightCard(
            title = "💡 Smart Advice",
            content = insight.advice,
            accentColor = Zinc.copy(alpha = 0.08f),
            borderColor = Zinc.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun InsightStatCard(
    modifier: Modifier,
    label: String,
    value: String,
    valueColor: Color,
    emoji: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8F8F8))
            .padding(16.dp)
    ) {
        ExpenseTextView(text = emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        ExpenseTextView(text = label, fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        ExpenseTextView(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
fun InsightCard(
    title: String,
    content: String,
    contentStyle: String = "normal",
    accentColor: Color = Color(0xFFF8F8F8),
    borderColor: Color = Color.Transparent
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(accentColor)
            .then(
                if (borderColor != Color.Transparent)
                    Modifier.padding(1.dp).clip(RoundedCornerShape(13.dp)).background(accentColor)
                else Modifier
            )
            .padding(16.dp)
    ) {
        ExpenseTextView(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(10.dp))
        if (contentStyle == "large") {
            ExpenseTextView(
                text = content,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Zinc
            )
        } else {
            ExpenseTextView(text = content, fontSize = 14.sp, color = Color(0xFF333333))
        }
    }
}