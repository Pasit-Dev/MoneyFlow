package com.example.moneyflow_jetpackcompose.screen.setting_screen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moneyflow_jetpackcompose.component.TopBar
import com.example.moneyflow_jetpackcompose.ui.theme.DarkBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.PrimaryColor
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun AppearanceScreen(navController: NavController) {
    val context = LocalContext.current
    var themePreference = ThemePreference(context)
    val themeMode = themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val selectedTheme = themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value)
    fun onThemeSelect(themeMode: ThemeMode) {
        CoroutineScope(Dispatchers.IO).launch {
            themePreference.saveTheme(themeMode.value)
        }
    }

    Scaffold(
        containerColor = if (isDarkTheme) DarkBackgroundColor else WhiteBackgroundColor,
        topBar = {
            TopBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint =  if (isDarkTheme) Color.White else Color.Black
                        )
                    }
                },
                title = "Appearance"
            )
        }
    ) {  innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable{
                    onThemeSelect(ThemeMode.SYSTEM)
                }.padding(horizontal = 16.dp)
            ) {

                Text(text = "System Theme", color = if (isDarkTheme) Color.White else Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                RadioButton(
                    colors = RadioButtonColors(
                        unselectedColor = Color.DarkGray,
                        selectedColor = PrimaryColor,
                        disabledSelectedColor = Color.LightGray,
                        disabledUnselectedColor = Color.LightGray
                    ),
                    selected = selectedTheme.value == ThemeMode.SYSTEM.value,
                    onClick = { onThemeSelect(ThemeMode.SYSTEM)

                    }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable{
                    onThemeSelect(ThemeMode.LIGHT)
                }.padding(horizontal = 16.dp)
            ) {

                Text(text = "Light Theme", color = if (isDarkTheme) Color.White else Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                RadioButton(
                    colors = RadioButtonColors(
                        unselectedColor = Color.DarkGray,
                        selectedColor = PrimaryColor,
                        disabledSelectedColor = Color.LightGray,
                        disabledUnselectedColor = Color.LightGray
                    ),
                    selected = selectedTheme.value == ThemeMode.LIGHT.value,
                    onClick = { onThemeSelect(ThemeMode.LIGHT)

                    }
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable{
                    onThemeSelect(ThemeMode.DARK)
                }.padding(horizontal = 16.dp)
            ) {

                Text(text = "Dark Theme", color = if (isDarkTheme) Color.White else Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                RadioButton(
                    colors = RadioButtonColors(
                        unselectedColor = Color.DarkGray,
                        selectedColor = PrimaryColor,
                        disabledSelectedColor = Color.LightGray,
                        disabledUnselectedColor = Color.LightGray
                    ),
                    selected = selectedTheme.value == ThemeMode.DARK.value,
                    onClick = { onThemeSelect(ThemeMode.DARK)
                    }
                )
            }
        }
    }
}

