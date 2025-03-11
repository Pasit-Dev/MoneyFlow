package com.example.moneyflow_jetpackcompose.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.moneyflow_jetpackcompose.api.ApiClient
import com.example.moneyflow_jetpackcompose.api.TransactionRequest
import com.example.moneyflow_jetpackcompose.model.TransactionModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TransactionViewModel: ViewModel() {
    private val api = ApiClient.transactionApi

    private val _transactions = MutableStateFlow<List<TransactionModel>>(emptyList())
    val transactions: StateFlow<List<TransactionModel>> = _transactions


    private val _groupedTransactions = MutableStateFlow<Map<String, Pair<List<TransactionModel>, Double>>>(emptyMap())
    val groupedTransactions: StateFlow<Map<String, Pair<List<TransactionModel>, Double>>> = _groupedTransactions

    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount: StateFlow<Double> = _totalAmount

    private val _expense = mutableDoubleStateOf(0.00)
    val expense: State<Double> = _expense

    private val _incomeList =  derivedStateOf {
        _transactions.value.filter { it.type == "income" }
    }
    val incomeList: State<List<TransactionModel>> = _incomeList
    private val _income = mutableDoubleStateOf(0.00)
    val income: State<Double> = _income


    private val _netBalance = mutableDoubleStateOf(0.00)
    val netBalance: State<Double> = _netBalance

    fun groupTransaction(transactions: List<TransactionModel>, selectType: String) {
        val grouped = transactions
            .filter { it.type == selectType }
            .groupBy { it.categoryName ?: "Uncategorized" }
            .mapValues { entry ->
                entry.value to entry.value.sumOf { it.amount }
            }

        // อัปเดตค่าใน StateFlow
        _groupedTransactions.value = grouped
        _totalAmount.value = grouped.values.sumOf { it.second }
    }

    fun fetchTransactionByMonth(userId: String, month: String, year: String) {
        viewModelScope.launch {
            Log.d("API", "Send data $month, $year")
            try {
                val response = api.getTransactionByMonth(userId, month, year)
                if (response.isSuccessful) {
                    response.body()?.data?.let { transactions ->
                        withContext(Dispatchers.IO) {
                            _transactions.value = transactions
                            updateTransactionTotals()
                            groupTransaction(_transactions.value, "income")
                        }.let {
                            Log.d("API", "Get Transaction by month successful")
                        }

                    }
                } else {
                    Log.e("API", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }


    fun createTransaction(transactionRequest: TransactionRequest, navController: NavController) {
        viewModelScope.launch {
            Log.d("API", "DATA: $transactionRequest")
            try {
                val response = api.createTransaction(transactionRequest)
                if (response.isSuccessful) {
                    Log.d("API", "Response is successful")
                    if (response.code() == 200) {
                        response.body()?.data?.let { it ->
                            _transactions.value += it
                            Log.e("API", "Successful: ${_transactions.value}")
                            navController.popBackStack()
                        }
                    } else {
                        Log.e("API", "Error: ${response.code()}")
                    }
                } else {
                    Log.e("API", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun updateTransaction(id: Int, transactionRequest: TransactionRequest, navController: NavController) {
        viewModelScope.launch {
            try {
                val response = api.updateTransaction(id, transactionRequest)
                if (response.isSuccessful) {
                    response.body()?.data?.let { updateTransaction ->
                        _transactions.value = _transactions.value.map { oldTranasction ->
                            if (oldTranasction.id == id) updateTransaction else oldTranasction

                        }
                        Log.d("API", "Transaction update successfully")
                        navController.popBackStack()
                    }
                } else {
                    Log.e("API", "Error Response : ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun deleteTransaction(id: Int, accountViewModel: AccountViewModel, userId: String) {
        viewModelScope.launch {
            try {
                val response = api.deleteTransaction(id)
                if (response.isSuccessful) {
                    response.body()?.data.let {
                        withContext(Dispatchers.Main) {
                            // ลบรายการจาก transactions
                            _transactions.value = _transactions.value.filter { it.id != id }
                            accountViewModel.calculateBalance(userId)

                            // คำนวณยอดใหม่
                            updateTransactionTotals()
                        }
                    }
                } else {
                    Log.e("API", "Error Response Code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }
    private fun updateTransactionTotals() {
        // คำนวณยอดรวม expense และ income ใหม่
        val expenseTotal = _transactions.value.filter { it.type == "expense" }.sumOf { it.amount }
        val incomeTotal = _transactions.value.filter { it.type == "income" }.sumOf { it.amount }

        // คำนวณยอดสุทธิ (Net Balance)
        val netBalance = incomeTotal - expenseTotal

        // อัพเดทค่าใน StateFlow เพื่อให้ UI อัปเดต
        _expense.value = expenseTotal
        _income.value = incomeTotal
        _netBalance.value = netBalance
    }
}