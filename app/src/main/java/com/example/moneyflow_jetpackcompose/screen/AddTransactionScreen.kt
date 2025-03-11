package com.example.moneyflow_jetpackcompose.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.moneyflow_jetpackcompose.R
import com.example.moneyflow_jetpackcompose.api.TransactionRequest
import com.example.moneyflow_jetpackcompose.component.AmountText
import com.example.moneyflow_jetpackcompose.component.InputField
import com.example.moneyflow_jetpackcompose.component.NumericKeypad
import com.example.moneyflow_jetpackcompose.component.SelectedButton
import com.example.moneyflow_jetpackcompose.component.TopBar
import com.example.moneyflow_jetpackcompose.datastore.DataStoreManager
import com.example.moneyflow_jetpackcompose.model.AccountModel
import com.example.moneyflow_jetpackcompose.model.CategoryModel
import com.example.moneyflow_jetpackcompose.ui.theme.AccountColor
import com.example.moneyflow_jetpackcompose.ui.theme.BlackColor
import com.example.moneyflow_jetpackcompose.ui.theme.CategoryColor
import com.example.moneyflow_jetpackcompose.ui.theme.DateColor
import com.example.moneyflow_jetpackcompose.ui.theme.GrayColor
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteBackgroundColor
import com.example.moneyflow_jetpackcompose.ui.theme.WhiteColor
import com.example.moneyflow_jetpackcompose.viewmodel.AccountViewModel
import com.example.moneyflow_jetpackcompose.viewmodel.CategoryViewModel
import com.example.moneyflow_jetpackcompose.viewmodel.TransactionViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavController, accountViewModel: AccountViewModel = viewModel(), categoryViewModel: CategoryViewModel = viewModel(), transactionViewModel: TransactionViewModel =  viewModel()) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var amount by remember { mutableStateOf("0.00") }
    var note by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Expense", "Income", "Transfer")

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val formatter = DateTimeFormatter.ofPattern("dd, MMM yyyy")
    val formattedDate = selectedDate.format(formatter)

    var showFromAccountMenu by remember { mutableStateOf(false) }
    var selectedFromAccount by remember { mutableStateOf<AccountModel?>(null) }

    var showToAccountMenu by remember { mutableStateOf(false) }
    var selectedToAccount by remember { mutableStateOf<AccountModel?>(null) }

    var showCategoryMenu by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<CategoryModel?>(null) }

    var accountList by remember { mutableStateOf<List<AccountModel>>(emptyList()) }
    var expenseCategory by remember { mutableStateOf<List<CategoryModel>>(emptyList()) }
    var incomeCategory by remember { mutableStateOf<List<CategoryModel>>(emptyList()) }
    val userId by produceState(initialValue = "") {
        value = DataStoreManager.getToken(context) ?: ""
    }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            // เรียกข้อมูลบัญชีและประเภท
            accountViewModel.fetchAccount(userId)
            categoryViewModel.fetchCategories(context)
        }
    }

    LaunchedEffect(accountViewModel.accounts.value) {
        accountList = accountViewModel.accounts.value

    }
    LaunchedEffect(categoryViewModel.categories.value) {
        expenseCategory = categoryViewModel.categories.value.filter { it.type == "expense" }
        incomeCategory = categoryViewModel.categories.value.filter { it.type == "income" }
    }
    Scaffold(
        modifier = Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
            focusManager.clearFocus()
        },
        containerColor = WhiteBackgroundColor,
        topBar = {
            TopBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = "Add Transaction",
                actions = {}
            )
        }
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            // Transaction Type Tabs
            TabRow(
                containerColor = Color.Transparent,
                selectedTabIndex = selectedTab,
                divider = {},
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp).clip(RoundedCornerShape(100)).padding(4.dp),
                indicator = {
                    Box {}
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index
                            selectedFromAccount = null
                            selectedCategory = null
                        },
                        text = { Text(text = title, color = if (selectedTab == index) WhiteColor else GrayColor) },
                        modifier = if (selectedTab == index) Modifier.clip(RoundedCornerShape(50)).background(
                            BlackColor)
                        else Modifier.clip(RoundedCornerShape(50)).background(Color.Transparent)
                    )
                }
            }

            // Account and Category Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(modifier = Modifier) {
                    SelectedButton(
                        title = "Account",
                        value = selectedFromAccount?.name ?: "",
                        onClick = { showFromAccountMenu = true },
                        icon = R.drawable.bank_card_fill,
                        color = AccountColor,
                    )
                    DropdownMenu(expanded = showFromAccountMenu, onDismissRequest = { showFromAccountMenu = false}) {
                       accountList.filter { it.id != selectedToAccount?.id?: 0 } .forEach { account ->
                            DropdownMenuItem(
                                text = { Text(text = account.name) },
                                onClick = {
                                    selectedFromAccount = account
                                    showFromAccountMenu = false
                                }
                            )
                        }
                    }
                }
                if (selectedTab == 2) {
                    Box(modifier = Modifier) {
                        SelectedButton(
                            title = "To account",
                            value = selectedToAccount?.name?: "",
                            onClick = { showToAccountMenu = true },
                            icon = R.drawable.bank_card_fill,
                            color = AccountColor,
                        )

                        DropdownMenu(expanded = showToAccountMenu, onDismissRequest = { showToAccountMenu = false}) {
                           accountList.filter { it.id != selectedFromAccount?.id?: 0 } .forEach { account ->
                                DropdownMenuItem(
                                    text = { Text(text = account.name) },
                                    onClick = {
                                        selectedToAccount = account
                                        showToAccountMenu = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier) {
                        SelectedButton(
                            title = "Category",
                            value = selectedCategory?.categoryName?: "",
                            onClick = { showCategoryMenu = true },
                            icon = R.drawable.grid_fill,
                            color = CategoryColor,
                        )
                        DropdownMenu(expanded = showCategoryMenu, onDismissRequest = { showCategoryMenu = false}) {
                            if (selectedTab == 0) expenseCategory.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(text = category.categoryName )},
                                    onClick = {
                                        selectedCategory = category
                                        showCategoryMenu = false
                                    }
                                )
                            } else incomeCategory.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(text = category.categoryName )},
                                    onClick = {
                                        selectedCategory = category
                                        showCategoryMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

            }
            // Amount Display
            AmountText(amount)
            // Note Input
            InputField(
                text = "Add note...",
                value = note,
                onValueChange = {
                    note = it
                }
            )
            // Number Pad
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SelectedButton(
                        title = "Select Date",
                        value = formattedDate,
                        icon = R.drawable.calendar_2_fill,
                        color = DateColor,
                        onClick = { showDatePicker = true }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    NumericKeypad(
                        onNumberClick = { digit ->
                            if (amount == "0.00") amount = digit
                            else amount += digit
                        },
                        onDeleteClick = {
                            if (amount != "0.00") {
                                amount = if (amount.length > 1) amount.dropLast(1)
                                else "0.00"
                            }
                        },
                        onConfirmClick = {
                            val formattedDate =  selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            val isoDate = ZonedDateTime.of(LocalDate.parse(formattedDate).atStartOfDay(), ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                            val newTransactionRequest =  TransactionRequest(
                                name = if (note.isNullOrEmpty()) "" else note,
                                amount = amount.toDouble(),
                                date = isoDate,
                                type = if (selectedTab == 0) "expense" else if (selectedTab == 1) "income" else "transfer",
                                accountId = selectedFromAccount?.id ?: null,
                                categoryId = selectedCategory?.categoryId ?: null,
                                toAccountId = selectedToAccount?.id ?: null,
                                goalId = null,
                                subscriptionId = null,
                                userId = userId
                            )
                            if (selectedTab == 0 || selectedTab == 1) {
                                if (!note.isNullOrEmpty() && selectedFromAccount != null && selectedCategory != null) {
                                    Log.d("ADD", "$newTransactionRequest")
                                    transactionViewModel.createTransaction(newTransactionRequest, navController)
                                }
                            } else if (selectedTab == 2) {
                                if (selectedToAccount != null && selectedFromAccount != null) {
                                    Log.d("ADD", "$newTransactionRequest")
                                    transactionViewModel.createTransaction(newTransactionRequest, navController)
                                }
                             } else {
                                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = selectedDate.atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                )
                DatePickerDialog(onDismissRequest = { showDatePicker = false}, confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val newDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                            selectedDate = newDate

                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                }, dismissButton = {
                    TextButton(onClick = { showDatePicker = false}) {
                        Text("Cancel")
                    }
                }) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )
                }
            }
        }
    }


}








@Preview (showBackground = true)
@Composable
fun AddTransactionScreenPreview() {
    AddTransactionScreen(rememberNavController())
}