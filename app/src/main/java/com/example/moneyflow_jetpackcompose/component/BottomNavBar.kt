package com.example.moneyflow_jetpackcompose.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moneyflow_jetpackcompose.R
import com.example.moneyflow_jetpackcompose.ui.theme.DarkBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.PrimaryColor
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor


sealed class BottomNavItem(val route: String,@DrawableRes val icon: Int, val label : String) {
    object Home: BottomNavItem("home", R.drawable.home_4_fill, "Home")
    object Subscription: BottomNavItem("subscription", R.drawable.bill_fill, "Subscription")
    object Goal: BottomNavItem("gaol", R.drawable.target_line, "Goal")
    object Report: BottomNavItem("report", R.drawable.chart_bar_fill, "Report")
    object Setting: BottomNavItem("setting", R.drawable.setting_fill, "Setting")
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Subscription,
        BottomNavItem.Goal,
        BottomNavItem.Report,
        BottomNavItem.Setting
    )
    val context = LocalContext.current
    val themeMode = ThemePreference(context).themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    BottomNavigation(backgroundColor = if (isDarkTheme) DarkBackgroundColor else WhiteBackgroundColor, modifier = Modifier.padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(selected = currentRoute == item.route, onClick = {

                navController.navigate(item.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
                icon = { Icon(
                    painter = painterResource(item.icon), contentDescription = null, modifier = Modifier.size(24.dp))},
                selectedContentColor = PrimaryColor,
                unselectedContentColor = Color(0xFF7F93AF),
                alwaysShowLabel = false

            )
        }
    }
}

@Preview (showBackground = true)
@Composable
fun BottomNavBarPreview() {
    BottomNavBar(rememberNavController())
}