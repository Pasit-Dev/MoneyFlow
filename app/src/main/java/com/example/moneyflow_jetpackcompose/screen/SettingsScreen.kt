package com.example.moneyflow_jetpackcompose.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moneyflow_jetpackcompose.R
import com.example.moneyflow_jetpackcompose.component.TopBar
import com.example.moneyflow_jetpackcompose.ui.theme.DarkBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor

@Composable
fun SettingsScreen(navController: NavController) {
    val themeMode = ThemePreference(context = LocalContext.current).themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    Scaffold(
        containerColor = if (isDarkTheme) DarkBackgroundColor else WhiteBackgroundColor,
        topBar = {
            TopBar(
                title = "Settings",
                actions = {}
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {

            SettingsItem(iconResId = R.drawable.wallet_4_fill, text = "Account Manage", onClick = {
                navController.navigate("accountManage")
            })
            SettingsItem(iconResId = R.drawable.grid_fill, text = "Category Manage", onClick = {
                navController.navigate("categoryManage")
            })
            SettingsItem(iconResId = R.drawable.brightness_line, text = "Appearance", onClick = {
                navController.navigate("appearance")
            })
            SettingsItem(iconResId = R.drawable.user_edit_fill, text = "Profile Manage", onClick = {
                navController.navigate("profileManage")
            })
        }
    }
}


@Composable
fun SettingsItem(iconResId: Int, text: String, onClick: () -> Unit) {
    val context = LocalContext.current
    var themePreference = ThemePreference(context)
    val themeMode = themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clip(shape = RoundedCornerShape(12.dp)).background(if (isDarkTheme) Color.Black else Color.White, shape = RoundedCornerShape(16.dp)).clickable {
            onClick()
        }.padding(16.dp)) {
        Box(
            modifier = Modifier.size(36.dp).background(color = Color(0xFF121212), shape = RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center
        ) {
            Icon(painter = painterResource(id = iconResId), contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text =text, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = if (isDarkTheme) Color.White else Color.Black)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = text)
    }

}

@Preview (showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(rememberNavController())
}