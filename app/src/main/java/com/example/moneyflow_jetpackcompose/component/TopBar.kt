package com.example.moneyflow_jetpackcompose.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneyflow_jetpackcompose.ui.theme.DarkBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, actions: @Composable() (RowScope.() -> Unit) = {}, navigationIcon:  @Composable () -> Unit = {}) {
    val themeMode = ThemePreference(context = LocalContext.current).themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = if (isDarkTheme) DarkBackgroundColor else WhiteBackgroundColor,),
        modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
        navigationIcon = navigationIcon,
        title = {
            Text(title, fontWeight = FontWeight.Medium)
        },
        actions = actions

    )
}