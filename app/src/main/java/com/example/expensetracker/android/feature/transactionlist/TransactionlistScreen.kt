package com.example.expensetracker.android.feature.transactionlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.android.R
import com.example.expensetracker.android.feature.add_expense.ExpenseDropDown
import com.example.expensetracker.android.feature.home.HomeViewModel
import com.example.expensetracker.android.feature.home.getCategoryIcon
import com.example.expensetracker.android.feature.stats.TopSpendingList
import com.example.expensetracker.android.network.ExpenseResponse
import com.example.expensetracker.android.ui.theme.Green
import com.example.expensetracker.android.ui.theme.LightGrey
import com.example.expensetracker.android.ui.theme.Red
import com.example.expensetracker.android.widget.ExpenseTextView

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionListScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var filterType by remember { mutableStateOf("All") }
    var dateRange by remember { mutableStateOf("All Time") }
    var menuExpanded by remember { mutableStateOf(false) }

    val filtered = uiState.expenses.filter { expense ->
        when (filterType) {
            "Expense" -> expense.type == "EXPENSE"
            "Income"  -> expense.type == "INCOME"
            else      -> true
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() },
                    colorFilter = ColorFilter.tint(Color.Black)
                )
                ExpenseTextView(
                    text = "Transactions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp).align(Alignment.Center)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { menuExpanded = !menuExpanded },
                    colorFilter = ColorFilter.tint(Color.Black)
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    AnimatedVisibility(
                        visible = menuExpanded,
                        enter = slideInVertically(initialOffsetY = { -it / 2 }),
                        exit  = slideOutVertically(targetOffsetY = { -it }),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column {
                            ExpenseDropDown(
                                listOfItems = listOf("All", "Expense", "Income"),
                                onItemSelected = { selected ->
                                    filterType = selected
                                    menuExpanded = false
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ExpenseDropDown(
                                listOfItems = listOf("All Time", "Yesterday", "Today", "Last 30 Days", "Last 90 Days", "Last Year"),
                                onItemSelected = { selected ->
                                    dateRange = selected
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }

                items(filtered) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .animateItemPlacement(tween(100))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = getCategoryIcon(item.category)),
                                contentDescription = null,
                                modifier = Modifier.size(51.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Column {
                                ExpenseTextView(
                                    text = item.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                ExpenseTextView(
                                    text = item.transactionDate ?: "",
                                    fontSize = 13.sp,
                                    color = LightGrey
                                )
                            }
                        }
                        ExpenseTextView(
                            text = (if (item.type == "INCOME") "+" else "-") + "$%.2f".format(item.amount),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.CenterEnd),
                            color = if (item.type == "INCOME") Green else Red
                        )
                    }
                }
            }
        }
    }
}