package com.example.expensetracker.android.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.android.R
import com.example.expensetracker.android.base.HomeNavigationEvent
import com.example.expensetracker.android.base.NavigationEvent
import com.example.expensetracker.android.network.ExpenseResponse
import com.example.expensetracker.android.ui.theme.Green
import com.example.expensetracker.android.ui.theme.LightGrey
import com.example.expensetracker.android.ui.theme.Red
import com.example.expensetracker.android.ui.theme.Typography
import com.example.expensetracker.android.ui.theme.Zinc
import com.example.expensetracker.android.widget.ExpenseTextView

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                NavigationEvent.NavigateBack -> navController.popBackStack()
                HomeNavigationEvent.NavigateToSeeAll -> navController.navigate("/all_transactions")
                HomeNavigationEvent.NavigateToAddIncome -> navController.navigate("/add_income")
                HomeNavigationEvent.NavigateToAddExpense -> navController.navigate("/add_exp")
                HomeNavigationEvent.NavigateToLogin -> navController.navigate("/login") {
                    popUpTo("/home") { inclusive = true }
                }
                else -> {}
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow, list, card, topBar, add) = createRefs()

            Image(
                painter = painterResource(id = R.drawable.ic_topbar),
                contentDescription = null,
                modifier = Modifier.constrainAs(topBar) {
                    top.linkTo(parent.top); start.linkTo(parent.start); end.linkTo(parent.end)
                }
            )

            // Header row with name + logout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(nameRow) {
                        top.linkTo(parent.top); start.linkTo(parent.start); end.linkTo(parent.end)
                    }
            ) {
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    ExpenseTextView(text = "Good day,", style = Typography.bodyMedium, color = Color.White)
                    ExpenseTextView(text = uiState.userName, style = Typography.titleLarge, color = Color.White)
                }
                // Logout button
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { viewModel.logout() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    ExpenseTextView(text = "Logout", fontSize = 12.sp, color = Color.White)
                }
            }

            // Balance card
            CardItem(
                modifier = Modifier.constrainAs(card) {
                    top.linkTo(nameRow.bottom); start.linkTo(parent.start); end.linkTo(parent.end)
                },
                balance = viewModel.getBalance(uiState.expenses),
                income = viewModel.getTotalIncome(uiState.expenses),
                expense = viewModel.getTotalExpense(uiState.expenses)
            )

            // Transaction list
            TransactionList(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(list) {
                        top.linkTo(card.bottom); start.linkTo(parent.start)
                        end.linkTo(parent.end); bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    },
                list = uiState.expenses,
                isLoading = uiState.isLoading,
                onSeeAllClicked = { viewModel.onEvent(HomeUiEvent.OnSeeAllClicked) },
                onInsightsClicked = { navController.navigate("/insights") }
            )

            // FAB
            Box(
                modifier = Modifier.fillMaxSize().constrainAs(add) {
                    bottom.linkTo(parent.bottom); end.linkTo(parent.end)
                },
                contentAlignment = Alignment.BottomEnd
            ) {
                MultiFloatingActionButton(
                    modifier = Modifier,
                    onAddExpenseClicked = { viewModel.onEvent(HomeUiEvent.OnAddExpenseClicked) },
                    onAddIncomeClicked = { viewModel.onEvent(HomeUiEvent.OnAddIncomeClicked) }
                )
            }
        }
    }
}

@Composable
fun TransactionList(
    modifier: Modifier,
    list: List<ExpenseResponse>,
    isLoading: Boolean = false,
    title: String = "Recent Transactions",
    onSeeAllClicked: () -> Unit = {},
    onInsightsClicked: () -> Unit = {}
) {
    LazyColumn(modifier = modifier.padding(horizontal = 16.dp)) {
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExpenseTextView(text = title, style = Typography.titleLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // AI Insights button
                        if (title == "Recent Transactions") {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Zinc.copy(alpha = 0.1f))
                                    .clickable { onInsightsClicked() }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                ExpenseTextView(text = "✨ AI Insights", fontSize = 12.sp, color = Zinc)
                            }
                            ExpenseTextView(
                                text = "See all",
                                style = Typography.bodyMedium,
                                modifier = Modifier.clickable { onSeeAllClicked() }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(12.dp))
            }
        }

        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Zinc)
                }
            }
        } else {
            items(items = list, key = { it.id }) { item ->
                ApiTransactionItem(item = item)
            }
        }
    }
}

@Composable
fun ApiTransactionItem(item: ExpenseResponse) {
    val isIncome = item.type == "INCOME"
    val amount = if (isIncome) item.amount else -item.amount
    val color = if (isIncome) Green else Red
    val icon = getCategoryIcon(item.category)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(51.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                ExpenseTextView(text = item.title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.size(4.dp))
                ExpenseTextView(
                    text = item.category ?: item.type,
                    fontSize = 12.sp,
                    color = LightGrey
                )
                Spacer(modifier = Modifier.size(2.dp))
                ExpenseTextView(
                    text = item.transactionDate ?: "",
                    fontSize = 12.sp,
                    color = LightGrey
                )
            }
        }
        ExpenseTextView(
            text = (if (isIncome) "+" else "-") + "$%.2f".format(item.amount),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterEnd),
            color = color
        )
    }
}

// Map AI-assigned category names to existing drawable icons
fun getCategoryIcon(category: String?): Int {
    return when (category?.lowercase()) {
        "food", "dining out", "starbucks" -> R.drawable.ic_starbucks
        "shopping", "grocery"             -> R.drawable.ic_netflix
        "salary", "income", "paypal"      -> R.drawable.ic_paypal
        "entertainment", "netflix"        -> R.drawable.ic_netflix
        else                              -> R.drawable.ic_upwork
    }
}

// ── Reuse exact same FAB and CardItem from original ──────────────────────────

@Composable
fun MultiFloatingActionButton(
    modifier: Modifier,
    onAddExpenseClicked: () -> Unit,
    onAddIncomeClicked: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AnimatedVisibility(visible = expanded) {
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier.size(48.dp)
                            .background(color = Zinc, shape = RoundedCornerShape(12.dp))
                            .clickable { onAddIncomeClicked() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painter = painterResource(R.drawable.ic_income), contentDescription = "Add Income", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier.size(48.dp)
                            .background(color = Zinc, shape = RoundedCornerShape(12.dp))
                            .clickable { onAddExpenseClicked() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painter = painterResource(R.drawable.ic_expense), contentDescription = "Add Expense", tint = Color.White)
                    }
                }
            }
            Box(
                modifier = Modifier.padding(8.dp).size(60.dp)
                    .clip(RoundedCornerShape(16.dp)).background(color = Zinc)
                    .clickable { expanded = !expanded },
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(R.drawable.ic_addbutton), contentDescription = null, modifier = Modifier.size(40.dp))
            }
        }
    }
}

@Composable
fun CardItem(modifier: Modifier, balance: String, income: String, expense: String) {
    Column(
        modifier = modifier.padding(16.dp).fillMaxWidth().height(200.dp)
            .clip(RoundedCornerShape(16.dp)).background(Zinc).padding(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column {
                ExpenseTextView(text = "Total Balance", style = Typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.size(8.dp))
                ExpenseTextView(text = balance, style = Typography.headlineLarge, color = Color.White)
            }
            Image(painter = painterResource(id = R.drawable.dots_menu), contentDescription = null, modifier = Modifier.align(Alignment.CenterEnd))
        }
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            CardRowItem(modifier = Modifier.align(Alignment.CenterStart), title = "Income", amount = income, imaget = R.drawable.ic_income)
            CardRowItem(modifier = Modifier.align(Alignment.CenterEnd), title = "Expense", amount = expense, imaget = R.drawable.ic_expense)
        }
    }
}

@Composable
fun CardRowItem(modifier: Modifier, title: String, amount: String, imaget: Int) {
    Column(modifier = modifier) {
        Row {
            Image(painter = painterResource(id = imaget), contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            ExpenseTextView(text = title, style = Typography.bodyLarge, color = Color.White)
        }
        Spacer(modifier = Modifier.size(4.dp))
        ExpenseTextView(text = amount, style = Typography.titleLarge, color = Color.White)
    }
}