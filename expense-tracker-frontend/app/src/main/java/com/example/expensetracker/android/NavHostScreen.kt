package com.example.expensetracker.android

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.expensetracker.android.feature.add_expense.AddExpense
import com.example.expensetracker.android.feature.home.HomeScreen
import com.example.expensetracker.android.feature.stats.InsightsScreen
import com.example.expensetracker.android.feature.login.LoginScreen
import com.example.expensetracker.android.feature.stats.StatsScreen
import com.example.expensetracker.android.feature.transactionlist.TransactionListScreen
import com.example.expensetracker.android.network.TokenManager
import com.example.expensetracker.android.ui.theme.Zinc
@Composable
fun NavHostScreen(tokenManager: TokenManager) {
    val navController = rememberNavController()
    var bottomBarVisibility by remember { mutableStateOf(true) }

    // Start at login if not authenticated, home otherwise
    val startDestination = if (tokenManager.isLoggedIn()) "/home" else "/login"

    Scaffold(bottomBar = {
        AnimatedVisibility(visible = bottomBarVisibility) {
            NavigationBottomBar(
                navController = navController,
                items = listOf(
                    NavItem(route = "/home",  icon = R.drawable.ic_home),
                    NavItem(route = "/stats", icon = R.drawable.ic_stats)
                )
            )
        }
    }) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            composable("/login") {
                bottomBarVisibility = false
                LoginScreen(navController)
            }
            composable("/home") {
                bottomBarVisibility = true
                HomeScreen(navController)
            }
            composable("/add_income") {
                bottomBarVisibility = false
                AddExpense(navController, isIncome = true)
            }
            composable("/add_exp") {
                bottomBarVisibility = false
                AddExpense(navController, isIncome = false)
            }
            composable("/stats") {
                bottomBarVisibility = true
                StatsScreen(navController)
            }
            composable("/all_transactions") {
                bottomBarVisibility = true
                TransactionListScreen(navController)
            }
            // ✨ New: AI Insights screen
            composable("/insights") {
                bottomBarVisibility = false
                InsightsScreen(navController)
            }
        }
    }
}

data class NavItem(val route: String, val icon: Int)

@Composable
fun NavigationBottomBar(navController: NavController, items: List<NavItem>) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    BottomAppBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = null) },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Zinc,
                    unselectedIconColor = Color.Gray
                )
            )
        }
    }
}
