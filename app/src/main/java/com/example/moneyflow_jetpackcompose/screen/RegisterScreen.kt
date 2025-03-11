package com.example.moneyflow_jetpackcompose.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.moneyflow_jetpackcompose.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {

    val emailRequester = remember { FocusRequester()}
    val passwordRequester = remember { FocusRequester() }
    val confirmRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var registerEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        emailRequester.requestFocus()
    }
    Scaffold(modifier = Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { focusManager.clearFocus() }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize().padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment =  Alignment.CenterHorizontally
        ) {
            Text(text= "Register", fontSize = 32.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.padding(top = 16.dp))
            OutlinedTextField(
                value = registerEmail,
                onValueChange = { registerEmail = it },
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
                onValueChange = {
                    password = it
                },
                passwordVisible = passwordVisible,
                toggleVisible = {
                    passwordVisible = !passwordVisible
                },
                modifier = Modifier.focusRequester(passwordRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = {
                    confirmRequester.requestFocus()
                })
            )
            PasswordField(
                text = "Confirm Password",
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                },
                passwordVisible = confirmVisible,
                toggleVisible = {
                    confirmVisible = !confirmVisible
                },
                modifier = Modifier.focusRequester(confirmRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    if (registerEmail.isNullOrEmpty()) {
                        Toast.makeText(context, "Email field can't be empty", Toast.LENGTH_LONG).show()
                    } else if (password.isNullOrEmpty()) {
                        Toast.makeText(context, "Password field can't be empty", Toast.LENGTH_LONG).show()
                    } else if (confirmPassword.isNullOrEmpty()) {
                        Toast.makeText(context, "Confirm Password field can't be empty", Toast.LENGTH_LONG).show()
                    } else {
                        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
                        if (registerEmail.matches(emailRegex)) {
                            if (password == confirmPassword) {
//                           viewModel.register(context, email, password)
//                           when (authState) {
//                               is AuthState.Success -> Toast.makeText(context, "✅ Registration Successful", Toast.LENGTH_LONG).show()
//                               is AuthState.Error -> Toast.makeText(context, "${(authState as AuthState.Error).message}", Toast.LENGTH_LONG).show()
//                               else -> {}
//                           }
                                navController.navigate("home")
                            } else {
                                Toast.makeText(context, "Password not matching", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Email field isn't email pattern.", Toast.LENGTH_LONG).show()
                        }
                    }
                })
            )
            Spacer(
                modifier = Modifier.padding(top = 24.dp)
            )
            Button(onClick = {
                if (registerEmail.isNullOrEmpty()) {
                    Toast.makeText(context, "Email field can't be empty", Toast.LENGTH_LONG).show()
                } else if (password.isNullOrEmpty()) {
                    Toast.makeText(context, "Password field can't be empty", Toast.LENGTH_LONG).show()
                } else if (confirmPassword.isNullOrEmpty()) {
                    Toast.makeText(context, "Confirm Password field can't be empty", Toast.LENGTH_LONG).show()
                } else {
                    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
                    if (registerEmail.matches(emailRegex)) {
                        if (password == confirmPassword) {
                            viewModel.register(context, registerEmail, password, navController)
                        } else {
                            Toast.makeText(context, "Password not matching", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Email field isn't email pattern.", Toast.LENGTH_LONG).show()
                    }
                }
            }, modifier = Modifier.height(54.dp).padding(horizontal = 24.dp).fillMaxWidth(), shape = RoundedCornerShape(16)) {
                Text("Register")
            }
            Row(modifier = Modifier, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Already have an account?")
                TextButton(onClick = {
                    navController.popBackStack()
                }) {
                    Text("Login now.")
                }
            }

        }
    }

}

@Preview (showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(rememberNavController())
}