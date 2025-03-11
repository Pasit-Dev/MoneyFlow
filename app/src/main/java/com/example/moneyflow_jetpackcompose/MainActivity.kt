package com.example.moneyflow_jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.moneyflow_jetpackcompose.screen.MainScreen
import com.example.moneyflow_jetpackcompose.ui.theme.MoneyFlowJetpackComposeTheme
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference


class MainActivity : ComponentActivity() {
    private lateinit var themePreference: ThemePreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themePreference = ThemePreference(this)
        enableEdgeToEdge()
        setContent {
            val themeMode = themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
            val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
//            MoneyFlowJetpackComposeTheme(isDarkTheme) {
////                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
////                    HomeScreen(modifier = Modifier.padding(innerPadding).windowInsetsPadding(
////                        WindowInsets.systemBars), onThemeChange = { newThemeMode -> lifecycleScope.launch {
////                        themePreference.saveTheme(newThemeMode.value)
////                    }})
////
////                }
//
//                MainScreen()
//            }
            MainScreen()

        }
    }
}