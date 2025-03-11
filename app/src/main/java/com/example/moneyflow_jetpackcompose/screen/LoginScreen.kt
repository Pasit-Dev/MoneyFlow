package com.example.moneyflow_jetpackcompose.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moneyflow_jetpackcompose.component.PasswordField
import com.example.moneyflow_jetpackcompose.ui.theme.DarkBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import com.example.moneyflow_jetpackcompose.viewmodel.AuthViewModel


@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current

    val emailRequester = remember { FocusRequester() }
    val passwordRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val themeMode = ThemePreference(context = LocalContext.current).themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        emailRequester.requestFocus()
    }


    Scaffold(
        containerColor = if (isDarkTheme) DarkBackgroundColor else WhiteBackgroundColor,
        modifier = Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
            focusManager.clearFocus()
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text= "Login", fontSize = 32.sp, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.padding(top = 16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).focusRequester(emailRequester),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    passwordRequester.requestFocus()
                })
            )
            PasswordField(
                text = "Password",
                value = password,
                onValueChange = { password = it },
                passwordVisible = passwordVisible,
                toggleVisible = {
                    passwordVisible = !passwordVisible
                },
              keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                  imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    if (email.isNullOrEmpty()) {
                        Toast.makeText(context, "Email field can't be empty", Toast.LENGTH_LONG).show()
                    } else if (password.isNullOrEmpty()) {
                        Toast.makeText(context, "Password field can't be empty", Toast.LENGTH_LONG).show()
                    } else {
                        if (isValidEmail(email.trim())) {
                            navController.navigate("home")
                        } else {
                            Toast.makeText(context, "Email field isn't email pattern.", Toast.LENGTH_LONG).show()
                        }
                    }
                }),
                modifier = Modifier.focusRequester(passwordRequester)
            )
//            Spacer(
//                modifier = Modifier.padding(top = 24.dp)
//            )
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd ) {
                TextButton(onClick = {
                    showForgotPasswordDialog = true
                }) { Text("Forgot password") }
            }
            Button(onClick = {
                if (email.isNullOrEmpty()) {
                    Toast.makeText(context, "Email field can't be empty", Toast.LENGTH_LONG).show()
                } else if (password.isNullOrEmpty()) {
                    Toast.makeText(context, "Password field can't be empty", Toast.LENGTH_LONG).show()
                } else {
                    if (isValidEmail(email)) {
                        viewModel.login(context, email.trim(), password.trim(), navController)
                    } else {
                        Toast.makeText(context, "Email field isn't email pattern.", Toast.LENGTH_LONG).show()
                    }
                }
            },modifier = Modifier.height(54.dp).padding(horizontal = 24.dp).fillMaxWidth(), shape = RoundedCornerShape(16)) {
                Text("Login")
            }
            Row(modifier = Modifier, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Don't have an account?")
                TextButton(onClick = {
                    navController.navigate("register")
                }) {
                    Text("Register now.")
                }
            }
        }
    }

    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(onDismiss = { showForgotPasswordDialog = false}, onSubmit = { mail ->
            viewModel.forgotPassword(context, mail)
            showForgotPasswordDialog = false
        })
    }

}

@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Forgot Password") },
        text = {
            Column {
                Text(
                    "Please enter your email address to receive a password reset link.",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        isEmailError = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    singleLine = true,
                    isError = isEmailError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    supportingText = if (isEmailError) {
                        { Text("Please enter a valid email address") }
                    } else null
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Basic email validation
                    if (email.contains('@') && email.isNotEmpty()) {
                        onSubmit(email)
                    } else {
                        isEmailError = true
                    }
                }
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


fun isValidEmail(email: String) : Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    return email.matches(emailRegex)
}


@Preview (showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(rememberNavController())
}