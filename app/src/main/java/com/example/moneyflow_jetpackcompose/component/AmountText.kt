package com.example.moneyflow_jetpackcompose.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference

@Composable
fun AmountText(value: String) {
    val context = LocalContext.current
    var themePreference = ThemePreference(context)
    val themeMode = themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    Text(
        "฿$value",
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 24.dp),
        color = if (isDarkTheme) Color.White else Color.Black
    )
}