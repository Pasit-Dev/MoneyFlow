package com.example.moneyflow_jetpackcompose.screen


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moneyflow_jetpackcompose.R
import com.example.moneyflow_jetpackcompose.component.DataNotFound
import com.example.moneyflow_jetpackcompose.component.MonthYearPickerDialog
import com.example.moneyflow_jetpackcompose.datastore.DataStoreManager
import com.example.moneyflow_jetpackcompose.model.TransactionModel
import com.example.moneyflow_jetpackcompose.ui.theme.BlackColor
import com.example.moneyflow_jetpackcompose.ui.theme.ErrorColor
import com.example.moneyflow_jetpackcompose.ui.theme.GrayColor
import com.example.moneyflow_jetpackcompose.ui.theme.PrimaryColor
import com.example.moneyflow_jetpackcompose.ui.theme.SuccessColor
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteColor
import com.example.moneyflow_jetpackcompose.ui.theme.parse
import com.example.moneyflow_jetpackcompose.utils.formatDate
import com.example.moneyflow_jetpackcompose.viewmodel.AccountViewModel
import com.example.moneyflow_jetpackcompose.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.format.TextStyle

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TransactionScreen(
    navController: NavController,
    accountViewModel: AccountViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val context = LocalContext.current
    var selectedMonth by remember { mutableStateOf(LocalDate.now().month) }
    var selectedYear by remember { mutableIntStateOf(Year.now().value) }
    var showDialog by remember { mutableStateOf(false) }
    val balance by accountViewModel.balance

    // โหลด userId จาก DataStoreManager
    var userId by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        userId = DataStoreManager.getToken(context) ?: ""
    }

    // ดึงข้อมูลเมื่อ userId หรือเดือน/ปีเปลี่ยน
    LaunchedEffect(userId, selectedMonth, selectedYear) {
        if (userId.isNotEmpty()) {
            transactionViewModel.fetchTransactionByMonth(
                userId,
                selectedMonth.getDisplayName(TextStyle.SHORT,androidx.compose.ui.text.intl. Locale.current.platformLocale),
                selectedYear.toString()
            )
            accountViewModel.calculateBalance(userId)
        }
    }

    Scaffold(
        containerColor = WhiteBackgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addTransaction")
                },
                shape = RoundedCornerShape(16.dp),
                backgroundColor = PrimaryColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Header(
                selectedMonth,
                selectedYear,
                onClick = { showDialog = true },
                balance
            )

            if (showDialog) {
                MonthYearPickerDialog(
                    showDialog = showDialog,
                    onDismiss = { showDialog = false },
                    onConfirm = { month, year ->
                        selectedMonth = month
                        selectedYear = year
                        showDialog = false
                    }
                )
            }

            BalanceSection(transactionViewModel)
            if (transactionViewModel.transactions.value.isEmpty()) {
                DataNotFound()
            } else {
                TransactionList(transactionViewModel, accountViewModel, userId, navController)
            }
        }
    }
}


@Composable
private fun Header(month: Month, year: Int,onClick: () -> Unit, balance: Double) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "ยอดเงินคงเหลือ",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "$balance Baht",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = { onClick() }) {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${month.getDisplayName(TextStyle.SHORT, androidx.compose.ui.text.intl.Locale.current.platformLocale)} $year",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, Modifier.size(32.dp), tint= BlackColor)
            }
        }
    }
}

@Composable
fun BalanceSection(transactionViewModel: TransactionViewModel) {
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Bottom) {
            Text(
                text = transactionViewModel.netBalance.value.toString(),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Spacer(Modifier.width(8.dp))
            Text(text = "THB",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.LightGray,
                )
        }
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(24.dp).background(SuccessColor, shape = RoundedCornerShape(4.dp))) {
                    Icon(
                        painter = painterResource(id = R.drawable.trending_up_line),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = WhiteColor
                        )
                }
                Spacer(Modifier.width(4.dp))
                Text("${transactionViewModel.income.value} THB", color = GrayColor)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(24.dp).background(ErrorColor, shape = RoundedCornerShape(4.dp))) {
                    Icon(
                        painter = painterResource(id = R.drawable.trending_down_line),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = WhiteColor
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text("${transactionViewModel.expense.value} THB", color = GrayColor)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

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
                            style = MaterialTheme.typography.titleMedium
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
    ListItem(
        modifier = Modifier.clickable {
            navController.currentBackStackEntry?.savedStateHandle?.set("transactionModel", transaction)
            navController.navigate("editTransaction")
        },
        colors = ListItemDefaults.colors(WhiteBackgroundColor),
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
            Text(if (transaction.name.isNullOrEmpty()) "Transfer" else transaction.name)
        },
        supportingContent =  {
            Text(formatDate(transaction.date))
        },
        trailingContent = {
            Text("${transaction.amount} THB", fontSize = 18.sp, color = if (transaction.type == "income") SuccessColor else if (transaction.type == "expense") ErrorColor else Color.Black )
        }
    )
}

@Preview (showBackground = true)
@Composable
fun TransactionScreenPreview() {
    TransactionScreen(navController= rememberNavController())
}