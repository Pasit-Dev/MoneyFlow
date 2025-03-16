package com.example.moneyflow_jetpackcompose.screen.setting_screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.moneyflow_jetpackcompose.component.TopBar
import com.example.moneyflow_jetpackcompose.datastore.DataStoreManager
import com.example.moneyflow_jetpackcompose.ui.theme.DarkBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.PrimaryColor
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteColor
import com.example.moneyflow_jetpackcompose.utils.base64ToBitmap
import com.example.moneyflow_jetpackcompose.utils.uriToBase64
import com.example.moneyflow_jetpackcompose.viewmodel.AuthViewModel

@Composable
fun ProfileManageScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var focusManager = LocalFocusManager.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var context = LocalContext.current
    var themePreference = ThemePreference(context)
    val themeMode = themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val userId by produceState(initialValue = "") {
        value = DataStoreManager.getToken(context) ?: ""
    }
    var email by remember { mutableStateOf("") }

    var showChangeEmailDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var newEmail by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var imgUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedImageUri = uri

            selectedImageUri?.let {
                authViewModel.updateBase64String(uriToBase64(it, context)!!)
            }
        }
    )

    LaunchedEffect(Unit) {
        authViewModel.getUser(context, userId)
        kotlinx.coroutines.delay(500)
        email = authViewModel.email.value.ifEmpty {
            "Not found"
        }
        imgUrl = authViewModel.imgProfile.value
    }


    LaunchedEffect(authViewModel.email.value) {
        email = authViewModel.email.value
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
                            tint = if (isDarkTheme) Color.White else Color.Black
                        )
                    }
                },
                title = "Profile Manage",
                actions = {
                    TextButton(onClick = {
                        showLogoutDialog = true
                    }) {
                        Text(text = "Logout", color = Color.Red, fontWeight = FontWeight.Medium)
                    }
                }
            )
        }, modifier = Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
            focusManager.clearFocus()
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
            ) {
                // แสดงรูปภาพด้วย AsyncImage
                if (base64ToBitmap(imgUrl) != null) {
                    selectedImageUri?.let { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier.size(120.dp).clip(CircleShape),
                        )
                    } ?: run {
                        Image(
                            bitmap = base64ToBitmap(imgUrl)!!.asImageBitmap(),
                            contentDescription = "Profile Image",
                            modifier = Modifier.size(120.dp).clip(CircleShape),
                        )
                    }
                } else {
                    Box(modifier = Modifier.size(120.dp)) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                // เพิ่ม IconButton ที่มุมล่างขวา
                IconButton(
                    onClick = { imagePickerLauncher.launch("image/") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(Color(0xFF4CAF50), shape = CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Profile Picture",
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(if (isDarkTheme) Color.Black else Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Email", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(email, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {
                        newEmail = email
                        showChangeEmailDialog = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Change Email")
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(if (isDarkTheme) Color.Black else Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Password", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("Change your password", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {
                        oldPassword = ""
                        newPassword = ""
                        confirmNewPassword = ""
                        showChangePasswordDialog = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Change Password")
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize().padding(bottom = 24.dp), contentAlignment = Alignment.BottomCenter) {
                authViewModel.base64String.value?.let {
                    Button(
                        onClick = {
                            authViewModel.updateImageProfile(context, userId, authViewModel.base64String.value!!)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                    ) {
                        Text("Update Profile Picture")
                    }
                }
            }

            // Change Email Dialog
            if (showChangeEmailDialog) {
                AlertDialog(
                    containerColor = WhiteColor,
                    onDismissRequest = { showChangeEmailDialog = false },
                    title = { Text("Change Email") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = newEmail,
                                onValueChange = { newEmail = it },
                                label = { Text("New Email") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Done
                                ),
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = "Email Icon")
                                }
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Implement email change functionality in your ViewModel
                                authViewModel.updateEmail(context, userId, newEmail)
                                showChangeEmailDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                        ) {
                            Text("Update Email")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showChangeEmailDialog = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    }
                )
            }

            // Change Password Dialog
            if (showChangePasswordDialog) {
                AlertDialog(
                    containerColor = WhiteColor,
                    modifier = Modifier.padding(16.dp),
                    onDismissRequest = { showChangePasswordDialog = false },
                    title = { Text("Change Password") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = oldPassword,
                                onValueChange = { oldPassword = it },
                                label = { Text("Current Password") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                maxLines = 1,
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = "Password Icon")
                                },
                                visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                                        Icon(
                                            imageVector = if (oldPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password visibility"
                                        )
                                    }
                                }
                            )

                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("New Password") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                maxLines = 1,
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = "Password Icon")
                                },
                                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                        Icon(
                                            imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password visibility"
                                        )
                                    }
                                }
                            )

                            OutlinedTextField(
                                value = confirmNewPassword,
                                onValueChange = { confirmNewPassword = it },
                                label = { Text("Confirm New Password") },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                singleLine = true,
                                maxLines = 1,
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = "Password Icon")
                                },
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                        Icon(
                                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password visibility"
                                        )
                                    }
                                },
                                isError = newPassword != confirmNewPassword && confirmNewPassword.isNotEmpty()
                            )

                            if (newPassword != confirmNewPassword && confirmNewPassword.isNotEmpty()) {
                                Text(
                                    text = "Passwords do not match",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newPassword == confirmNewPassword && newPassword.isNotEmpty()) {
                                    // Implement password change functionality in your ViewModel
                                    authViewModel.updatePassword(context, userId, oldPassword, newPassword)
                                    showChangePasswordDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                            enabled = newPassword == confirmNewPassword &&
                                    newPassword.isNotEmpty() &&
                                    oldPassword.isNotEmpty()
                        ) {
                            Text("Update Password")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showChangePasswordDialog = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    }
                )
            }

            if (showLogoutDialog) {
                AlertDialog(
                    containerColor = WhiteColor,
                    onDismissRequest = { showLogoutDialog = false},
                    title = { Text("Logout Confirmation") },
                    text = {
                        Text("Are you sure you want to logout?")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            authViewModel.logout(context, navController)
                            showLogoutDialog = false
                        }) {
                            Text("Yes", color = PrimaryColor)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showLogoutDialog = false
                        }) {
                            Text("No", color = Color.Gray)
                        }
                    }
                )
            }
        }
    }
}


@Preview (showBackground = true)
@Composable
fun ProfileManageScreenPreview() {
    ProfileManageScreen(rememberNavController())
}