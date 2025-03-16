package com.example.moneyflow_jetpackcompose.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneyflow_jetpackcompose.model.TransactionModel
import com.example.moneyflow_jetpackcompose.ui.theme.DarkBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.ErrorColor
import com.example.moneyflow_jetpackcompose.ui.theme.SuccessColor
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.parse
import com.example.moneyflow_jetpackcompose.utils.formatDate
import com.example.moneyflow_jetpackcompose.viewmodel.AccountViewModel
import com.example.moneyflow_jetpackcompose.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch


fun groupTransactionByDate(transaction: List<TransactionModel>): Map<String, Pair<Double, List<TransactionModel>>> {
    return transaction.groupBy { formatDate(it.date) }
        .toList() // convert to list so we can sort by date
        .sortedByDescending { it.first } // sort by the formatted date in descending order
        .toMap() // convert back to a map
        .mapValues { (_, transactions) ->
            val totalAmount = transactions.sumOf {
                when (it.type) {
                    "income" -> it.amount
                    "expense" -> -it.amount
                    else -> 0.0
                }
            }
            totalAmount to transactions
        }
        // Filter out groups with no transactions remaining
        .filter { it.value.second.isNotEmpty() }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TransactionList(viewModel: TransactionViewModel, accountViewModel: AccountViewModel, userId: String, navController: NavController) {
    val context = LocalContext.current
    var themePreference = ThemePreference(context)
    val themeMode = themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val scope = rememberCoroutineScope()

    // ใช้ collectAsState() เพื่อรับค่าจาก transactions ที่อยู่ใน StateFlow
    val transactions = viewModel.transactions.collectAsState().value

    // คำนวณ groupedTransactions เมื่อมีการเปลี่ยนแปลงใน transactions
    var groupedTransactions by remember { mutableStateOf(groupTransactionByDate(transactions)) }

    // ใช้ LaunchedEffect เพื่อคำนวณใหม่ทุกครั้งที่ transactions เปลี่ยนแปลง
    LaunchedEffect(transactions) {
        groupedTransactions = groupTransactionByDate(transactions)
    }

    LazyColumn {
        groupedTransactions.forEach { (date, totalAndTransactions) ->
            val (totalAmount, transactions) = totalAndTransactions

            // แสดงผลเฉพาะกลุ่มที่มีข้อมูล
            if (transactions.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.titleMedium.copy(color = if (isDarkTheme) Color.White else Color.Black)
                        )
                        Text(
                            text = "$totalAmount บาท",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (totalAmount >= 0) SuccessColor else ErrorColor
                        )
                    }
                }
                item {
                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // รายการ transaction
            items(transactions, key = { it.id }) { transaction ->
                var show by remember { mutableStateOf(true) }
                val dismissState = rememberDismissState(
                    confirmStateChange = { dismissValue ->
                        if (dismissValue == DismissValue.DismissedToStart) {
                            scope.launch {
                                viewModel.deleteTransaction(transaction.id, accountViewModel, userId)

                                show = false
                            }
                            true
                        } else {
                            false
                        }
                    }
                )

                if (show) {
                    SwipeToDismiss(
                        state = dismissState,
                        background = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(ErrorColor)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White
                                )
                            }
                        },
                        dismissContent = {
                            TransactionItem(transaction, navController)
                        },
                        directions = setOf(DismissDirection.EndToStart)
                    )
                }
            }
        }
    }
}







@Composable
fun TransactionItem(transaction: TransactionModel, navController: NavController) {
    val context = LocalContext.current
    var themePreference = ThemePreference(context)
    val themeMode = themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    ListItem(
        modifier = Modifier.clickable {
            navController.currentBackStackEntry?.savedStateHandle?.set("transactionModel", transaction)
            navController.navigate("editTransaction")
        },
        colors = ListItemDefaults.colors(if (isDarkTheme) DarkBackgroundColor else WhiteBackgroundColor),
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (transaction.color.isNullOrEmpty()) Color(0xFF121212) else Color.parse("#${transaction.color?.drop(2)}"),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = transaction.emoji?: "💸")
            }
        },
        headlineContent = {
            Text(if (transaction.name.isNullOrEmpty()) "Transfer" else transaction.name, color = if (isDarkTheme) Color.White else Color.Black)
        },
        supportingContent =  {
            Text(formatDate(transaction.date), color = if (isDarkTheme) Color.White else Color.Black)
        },
        trailingContent = {
            Text("${transaction.amount} THB", fontSize = 18.sp, color = if (transaction.type == "income") SuccessColor else if (transaction.type == "expense") ErrorColor else (if (isDarkTheme) Color.White else Color.White))
        }
    )
}