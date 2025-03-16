package com.example.moneyflow_jetpackcompose.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moneyflow_jetpackcompose.component.BottomNavBar
import com.example.moneyflow_jetpackcompose.component.BottomNavItem
import com.example.moneyflow_jetpackcompose.model.CategoryModel
import com.example.moneyflow_jetpackcompose.model.GoalModel
import com.example.moneyflow_jetpackcompose.model.SubscriptionModel
import com.example.moneyflow_jetpackcompose.model.TransactionModel
import com.example.moneyflow_jetpackcompose.screen.setting_screen.AccountManageScreen
import com.example.moneyflow_jetpackcompose.screen.setting_screen.AddAccountScreen
import com.example.moneyflow_jetpackcompose.screen.setting_screen.AddCategoryScreen
import com.example.moneyflow_jetpackcompose.screen.setting_screen.AppearanceScreen
import com.example.moneyflow_jetpackcompose.screen.setting_screen.CategoryManageScreen
import com.example.moneyflow_jetpackcompose.screen.setting_screen.EditCategoryScreen
import com.example.moneyflow_jetpackcompose.screen.setting_screen.ProfileManageScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen() {
    val navItemList = listOf(BottomNavItem.Home.route, BottomNavItem.Goal.route, BottomNavItem.Subscription.route, BottomNavItem.Report.route, BottomNavItem.Setting.route)
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentDestination = currentBackStackEntry?.destination
    val route = currentDestination?.route
    Scaffold(bottomBar = {
        if (route in navItemList) {
            BottomNavBar(navController)
        }
    }) { innerPadding ->
    NavHost(navController = navController, startDestination = BottomNavItem.Home.route, modifier = Modifier.fillMaxSize().padding(bottom = if (route in navItemList) 60.dp else innerPadding.calculateBottomPadding())) {
        composable(BottomNavItem.Home.route) {
            TransactionScreen(navController)
        }
        composable(BottomNavItem.Subscription.route) {
            SubscriptionScreen(navController)
        }
        composable(BottomNavItem.Goal.route) {
            GoalScreen(navController)
        }
        composable(BottomNavItem.Report.route) {
            ReportScreen(navController)
        }
        composable(BottomNavItem.Setting.route) {
            SettingsScreen(navController)
        }
        composable("addTransaction", enterTransition = {
            return@composable slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(500))
        }, exitTransition = {
            return@composable slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(500))
        },) {
            AddTransactionScreen(navController)
        }
        composable("accountManage") {
            AccountManageScreen(navController)
        }
        composable("categoryManage") {
            CategoryManageScreen(navController)
        }
        composable("appearance") {
            AppearanceScreen(navController)
        }
        composable("profileManage") {
            ProfileManageScreen(navController)
        }
        composable("addAccount", enterTransition = {
            return@composable slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(500))
        }, exitTransition = {
            return@composable slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(500))
        },) {
            AddAccountScreen(navController)
        }
        composable("addCategory", enterTransition = {
            return@composable slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(500))
        }, exitTransition = {
            return@composable slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(500))
        },) {
            AddCategoryScreen(navController)
        }
        composable("editTransaction") { currentBackStackEntry ->
            val transactionModel = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<TransactionModel>("transactionModel")
            EditTransactionScreen(navController, transactionModel)

        }
        composable("goalDetail") { backStackEntry ->
            val goalItem = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<GoalModel>("goalItem")
            GoalDetailsScreen(navController, goalItem)
        }
        composable("editCategory") { currentBackStackEntry ->
            val categoryItem = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<CategoryModel>("categoryModel")
            EditCategoryScreen(navController, categoryModel = categoryItem)

        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("addGoal") {
            AddGoalScreen(navController)
        }
        composable("addSubscription") {
            AddSubscriptionScreen(navController)
        }
        composable("editSubscription") { currentBackStackEntry ->
            val subscriptionItem = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<SubscriptionModel>("subscriptionModel")
            EditSubscriptionScreen(navController, subscriptionItem)
        }
    }

    }
}


