package com.example.moneyflow_jetpackcompose.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moneyflow_jetpackcompose.datastore.DataStoreManager

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()

    val userId by produceState<String?>(initialValue = null) {
        value = DataStoreManager.getToken(context) ?: ""
        Log.d("USER ID", "$value")
    }

    if (userId == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navController,
//            startDestination = "home"
            startDestination = if (userId.isNullOrEmpty()) "login" else "home"
        ) {
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
            composable("home") { HomeScreen() }
        }
    }
}