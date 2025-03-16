package com.example.moneyflow_jetpackcompose.screen


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moneyflow_jetpackcompose.component.DataNotFound
import com.example.moneyflow_jetpackcompose.component.ToggleButton
import com.example.moneyflow_jetpackcompose.component.TopBar
import com.example.moneyflow_jetpackcompose.datastore.DataStoreManager
import com.example.moneyflow_jetpackcompose.ui.theme.BlackColor
import com.example.moneyflow_jetpackcompose.ui.theme.DarkBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.PrimaryColor
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteColor
import com.example.moneyflow_jetpackcompose.utils.base64ToBitmap
import com.example.moneyflow_jetpackcompose.utils.formatDate
import com.example.moneyflow_jetpackcompose.utils.uriToBase64
import com.example.moneyflow_jetpackcompose.viewmodel.SubscriptionViewModel
import java.math.BigDecimal
import java.math.RoundingMode


@Composable
fun SubscriptionScreen(navController: NavController, subscriptionViewModel: SubscriptionViewModel = viewModel()) {
    val context = LocalContext.current
    var isSelected by remember { mutableStateOf("Month") }
    val themeMode = ThemePreference(context = LocalContext.current).themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    var userId by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        userId = DataStoreManager.getToken(context) ?: ""
    }

    // ดึงข้อมูลเมื่อ userId หรือเดือน/ปีเปลี่ยน
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            subscriptionViewModel.fetchSubscription(userId)
            kotlinx.coroutines.delay(300)

        }
    }
    var totalAmount by remember { mutableStateOf(0f) }



    LaunchedEffect(isSelected, subscriptionViewModel.subscriptions.value) {
        totalAmount = subscriptionViewModel.calculateTotalAmount(isSelected)
    }


    Scaffold(
        containerColor = if (isDarkTheme) DarkBackgroundColor else WhiteBackgroundColor,
        topBar = {
            TopBar(
                title = "Subscriptions",
                actions = {
                    ToggleButton(
                        text = "Month",
                        isSelected = isSelected == "Month",
                        onClick = { isSelected = "Month" }
                    )

                    Spacer(Modifier.width(8.dp))
                    ToggleButton(
                        text = "Year",
                        isSelected = isSelected == "Year",
                        onClick = { isSelected = "Year" }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("addSubscription")
            },
                containerColor = PrimaryColor) {
                Icon(Icons.Rounded.Add, contentDescription = null, tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(32.dp))
            Text(
                text = "฿${String.format("%.2f", totalAmount)} / $isSelected",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Red
            )
            Text(
                text = "All subscriptions cost",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))
            if (subscriptionViewModel.subscriptions.value.isEmpty()) {
                DataNotFound()
            } else {
                LazyColumn {
                    items(subscriptionViewModel.subscriptions.value) { subscription ->

                        ListItem(
                            modifier = Modifier.clickable {
                                navController.currentBackStackEntry?.savedStateHandle?.set("subscriptionModel", subscription)
                                navController.navigate("editSubscription")
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = if (isDarkTheme) DarkBackgroundColor else WhiteBackgroundColor,
                            ),
                            leadingContent = {
                                Box(
                                    modifier = Modifier.background(Color(0xFF121212), shape = RoundedCornerShape(12.dp)).padding(8.dp)
                                ) {
                                    if (base64ToBitmap(subscription.icon) != null) {
                                        Image(
                                            bitmap = base64ToBitmap(subscription.icon)!!.asImageBitmap(),
                                            contentDescription = "Profile Image",
                                            modifier = Modifier.size(24.dp).clip(RoundedCornerShape(16.dp))
                                        )
                                    } else {
                                        Box(modifier = Modifier.size(24.dp)) {
                                            CircularProgressIndicator(
                                                color = PrimaryColor,
                                                modifier = Modifier.align(Alignment.Center)
                                            )
                                        }
                                    }
                                }
                            },
                            headlineContent = {
                                Text(subscription.name, color = if (isDarkTheme) WhiteColor else BlackColor)
                            },
                            supportingContent = {
                                Text(formatDate(subscription.currentBill), color = if (isDarkTheme) WhiteColor else Color.Black)
                            },
                            trailingContent = {
                                Text("฿${subscription.amount} / ${if (subscription.frequency == "monthly") "m" else "y"}", fontSize = 18.sp, color = if (isDarkTheme) WhiteColor else Color.Black)
                            }
                        )
                    }
                }
            }
        }
    }
}






data class SubscriptionData(
    val icon: Int,
    val name: String,
    val date: String,
    val cost: String
)

// Sample data
val subscriptionItems = listOf(
    SubscriptionData(
        icon = 1,
        name = "Youtube Premium",
        date = "22 Dec 2024",
        cost = "฿55 / m"
    ),
    SubscriptionData(
        icon = 1,
        name = "Notion",
        date = "22 Dec 2025",
        cost = "฿1500 / y"
    ),
    SubscriptionData(
        icon = 1,
        name = "Netflix Clone",
        date = "22 Dec 2024",
        cost = "฿55 / m"
    )
    // Add more items as needed
)



@Preview (showBackground = true)
@Composable
fun SubscriptionScreenPreview() {
    SubscriptionScreen(rememberNavController())
}