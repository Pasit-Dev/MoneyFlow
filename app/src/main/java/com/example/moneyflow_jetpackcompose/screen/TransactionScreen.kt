package com.example.moneyflow_jetpackcompose.screen


import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
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
import com.example.moneyflow_jetpackcompose.ui.theme.BlackColor
import com.example.moneyflow_jetpackcompose.ui.theme.DarkBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.ErrorColor
import com.example.moneyflow_jetpackcompose.ui.theme.GrayColor
import com.example.moneyflow_jetpackcompose.ui.theme.PrimaryColor
import com.example.moneyflow_jetpackcompose.ui.theme.SuccessColor
import com.example.moneyflow_jetpackcompose.ui.theme.ThemeMode
import com.example.moneyflow_jetpackcompose.ui.theme.ThemePreference
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteColor
import com.example.moneyflow_jetpackcompose.viewmodel.AccountViewModel
import com.example.moneyflow_jetpackcompose.viewmodel.TransactionViewModel
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
    var themePreference = ThemePreference(context)
    val themeMode = themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
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
        containerColor = if (isDarkTheme) DarkBackgroundColor else WhiteBackgroundColor,
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
                com.example.moneyflow_jetpackcompose.component.TransactionList(transactionViewModel, accountViewModel, userId, navController)
            }
        }
    }
}


@Composable
private fun Header(month: Month, year: Int,onClick: () -> Unit, balance: Double) {
    val context = LocalContext.current
    var themePreference = ThemePreference(context)
    val themeMode = themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.value).value
    val isDarkTheme = when (ThemeMode.fromInt(themeMode)) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "ยอดเงินคงเหลือ",
            style = MaterialTheme.typography.bodyLarge.copy(color = if (isDarkTheme) Color.White else Color.Black)
        )
        Text(
            text = "$balance Baht",
            style = MaterialTheme.typography.headlineMedium.copy(color = if (isDarkTheme) Color.White else Color.Black),
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = { onClick() }) {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${month.getDisplayName(TextStyle.SHORT, androidx.compose.ui.text.intl.Locale.current.platformLocale)} $year",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) Color.White else Color.Black
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
                color = Color.LightGray,
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


@Preview (showBackground = true)
@Composable
fun TransactionScreenPreview() {
    TransactionScreen(navController= rememberNavController())
}