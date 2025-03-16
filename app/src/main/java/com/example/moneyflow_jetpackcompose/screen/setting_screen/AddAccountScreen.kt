package com.example.moneyflow_jetpackcompose.screen.setting_screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moneyflow_jetpackcompose.api.AccountRequest
import com.example.moneyflow_jetpackcompose.component.InputField
import com.example.moneyflow_jetpackcompose.component.NumericKeypad
import com.example.moneyflow_jetpackcompose.component.TopBar
import com.example.moneyflow_jetpackcompose.datastore.DataStoreManager
import com.example.moneyflow_jetpackcompose.ui.theme.DarkBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import com.example.moneyflow_jetpackcompose.viewmodel.AccountViewModel

@Composable
fun AddAccountScreen(navController: NavController, viewModel: AccountViewModel = viewModel()) {
    val context = LocalContext.current
    val themeMode = ThemePreference(context = context).themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var amount by remember { mutableStateOf("0.00") }
    var accountName by remember { mutableStateOf("") }
    val userId by produceState(initialValue = "") {
        value = DataStoreManager.getToken(context) ?: ""
    }


    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Scaffold(
        containerColor = if (isDarkTheme) DarkBackgroundColor else WhiteBackgroundColor,
        modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
            focusManager.clearFocus()
        },
        topBar = {
            TopBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDarkTheme) Color.White else Color.Black
                        )
                    }
                },
                title = "Add Account",
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(64.dp)) // Space at the top of the screen
            Text(amount, fontSize = 48.sp, fontWeight = FontWeight.Medium, color = if (amount == "0.00") Color.Gray else Color.Black)
            Spacer(Modifier.height(16.dp))
            Text("เงินตั้งต้น", color = Color.Gray)
            Spacer(Modifier.height(32.dp))
            InputField(
                text = "Enter account name",
                value = accountName,
                onValueChange = {
                    accountName = it
                },
                modifier = Modifier.focusRequester(focusRequester)
            )

            // NumericKeypad placed at the bottom
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(120.dp))
                    NumericKeypad(
                        onNumberClick = { digit ->
                            if (amount == "0.00") amount = digit
                            else amount += digit
                        },
                        onDeleteClick = {
                            if (amount != "0.00") {
                                if (amount.length > 1) amount = amount.dropLast(1)
                                else amount = "0.00"
                            }
                        },
                        onConfirmClick = {
                            if (!accountName.isNullOrEmpty()) {
                                viewModel.createAccount(
                                    AccountRequest(
                                        accountName,
                                        amount.toDouble(),
                                        userId
                                    ), navController
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Account name field can't be empty",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                    )
                }
            }
        }
    }
}


@Preview (showBackground = true)
@Composable
fun AddAccountScreenPreview() {
    AddAccountScreen(rememberNavController())
}