package com.example.moneyflow_jetpackcompose.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moneyflow_jetpackcompose.api.AccountRequest
import com.example.moneyflow_jetpackcompose.api.ApiClient
import com.example.moneyflow_jetpackcompose.model.AccountModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountViewModel : ViewModel() {
    private val api = ApiClient.accountApi

    private val _accounts = mutableStateOf<List<AccountModel>>(emptyList())
    val accounts: State<List<AccountModel>> = _accounts

    private val _balance = mutableStateOf(0.0)
    val balance : State<Double> = _balance

    private val _account = mutableStateOf<AccountModel?>(null)
    val account: State<AccountModel?> = _account
//    fun fetchAccount(userId: String) {
//       viewModelScope.launch {
//           try {
//               val response = api.getAccount(userId)
//               if (response.isSuccessful) {
//                   response.body()?.let {
//                       if (it.data != null) {
//                           if (it.data is List<AccountModel>) {
//                               _accounts.value = it.data
//                               Log.d("DATA", "${_accounts.value}")
//                           }
//                       }
//                   }
//               }
//           } catch (e: Exception) {
//               Log.e("API", "Error: ${e.localizedMessage}")
//           }
//       }
//    }

    suspend fun fetchAccount(userId: String) {
        try {
            val response = api.getAccount(userId)
            if (response.isSuccessful) {
                response.body()?.let {
                    if (it.data != null) {
                        if (it.data is List<AccountModel>) {
                            _accounts.value = it.data
                            Log.d("DATA", "Fetched Accounts: ${_accounts.value}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("API", "Error: ${e.localizedMessage}")
        }
    }

    fun fetchAccountById(id: Int) {
        viewModelScope.launch {
            try {
                val response = api.getAccountById(id)
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.data != null) {
                            _accounts.value += it.data
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun createAccount(newAccount: AccountRequest, navController: NavController) {
        viewModelScope.launch {
            try {
                val response = api.createAccount(newAccount)
                if (response.isSuccessful) {
                   response.body()?.let {
                       if (it.data != null) {
                           _accounts.value += it.data
                           navController.popBackStack()
                       }
                   }
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun updateAccount(id: Int, newAccount: AccountRequest) {
        viewModelScope.launch {
            try {
                val response = api.updateAccount(id, newAccount)
                if (response.isSuccessful) {
                    response.body()?.let { res ->
                        if (_account.value?.id == res.data?.id) {
                            _account.value = res.data
                        }
                        _accounts.value = _accounts.value.map {
                            if (it.id == res.data?.id) res.data else it
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun deleteAccount(id: Int) {
        viewModelScope.launch {
            try {
                val response = api.deleteAccount(id)
                if (response.isSuccessful) {
                    response.body()?.let { res ->
                       if (res.data != null) {
                           if (_account.value?.id == res.data.id) {
                               _account.value = null
                           }
                           _accounts.value = _accounts.value.filter { it.id != res.data.id }
                       }
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun calculateBalance(userId: String) {
        viewModelScope.launch {
            try {
                _balance.value = 0.0

                // เรียก fetchAccount และรอให้เสร็จก่อน
                fetchAccount(userId)

                // ตอนนี้ _accounts มีข้อมูลแล้ว คำนวณ balance
                _balance.value = _accounts.value.sumOf { it.balance }

                Log.d("DATA", "Calculate Balance ${_balance.value}")
            } catch (e: Exception) {
                Log.e("CALCULATE", "Error: ${e.localizedMessage}")
            }
        }
    }


}