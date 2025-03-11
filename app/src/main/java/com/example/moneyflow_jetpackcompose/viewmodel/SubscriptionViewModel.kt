package com.example.moneyflow_jetpackcompose.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.moneyflow_jetpackcompose.api.ApiClient
import com.example.moneyflow_jetpackcompose.api.SubscriptionRequest
import com.example.moneyflow_jetpackcompose.model.SubscriptionModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubscriptionViewModel: ViewModel() {
    private val api = ApiClient.subscriptionApi

    private val _subscriptions = mutableStateOf<List<SubscriptionModel>>(emptyList())
    val subscriptions: State<List<SubscriptionModel>> = _subscriptions

    private val _subscription = mutableStateOf<SubscriptionModel?>(null)
    val subscription: State<SubscriptionModel?> = _subscription

    fun fetchSubscription(userId: String) {
        viewModelScope.launch {
            try {
                val response = api.getSubscription(userId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.data != null) {
                            _subscriptions.value = it.data
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun fetchSubscriptionById(id: Int) {
        viewModelScope.launch {
            try {
                val response = api.getSubscriptionById(id)
                if (response.isSuccessful) {
                    response.body()?.let { res ->
                        _subscription.value = res.data
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun createSubscription(newSubscription: SubscriptionRequest, navController: NavController) {
        viewModelScope.launch {
            try {
                val response = api.createSubscription(newSubscription)
                if (response.isSuccessful) {
                    response.body()?.data?.let { res ->
                        withContext(Dispatchers.IO) {
                            _subscriptions.value += res
                        }
                        Log.d("API", "CREATE SUCCESSFUL")
                        navController.popBackStack()
                    }
                } else {
                    Log.e("API", "ERROR RESPONSE: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun calculateTotalAmount(isSelected: String): Float {
        Log.d("CalculateAmount", "Subscriptions List: ${_subscriptions.value}")
        val amounts = _subscriptions.value.map { subscription ->
            val calculatedAmount = when {
                isSelected.lowercase() == "month" && subscription.frequency.trim().equals("monthly", ignoreCase = true) -> subscription.amount
                isSelected.lowercase() == "month" && subscription.frequency.trim().equals("yearly", ignoreCase = true) -> subscription.amount / 12
                isSelected.lowercase() == "year" && subscription.frequency.trim().equals("monthly", ignoreCase = true) -> subscription.amount * 12
                isSelected.lowercase() == "year" && subscription.frequency.trim().equals("yearly", ignoreCase = true) -> subscription.amount
                else -> 0f
            }


            Log.d("CalculateAmount", "Subscription: ${subscription.name}, Frequency: ${subscription.frequency}, Amount: ${subscription.amount}, Calculated: $calculatedAmount")
            calculatedAmount
        }

        val total = amounts.reduceOrNull { acc, value -> acc + value } ?: 0f
        Log.d("CalculateAmount", "Total Amount: $total")

        return total
    }



    fun updateSubscription(id: Int, newSubscription: SubscriptionRequest, navController: NavController) {
        viewModelScope.launch {
            try {
                val response = api.updateSubscription(id, newSubscription)
                Log.d("API", "Section 1")
                if (response.isSuccessful) {
                    Log.d("API", "Section 2")
                    response.body()?.data?.let { res ->
                        Log.d("API", "Section 3: $res")
                        if (res!= null) {
                            withContext(Dispatchers.IO) {
                                _subscriptions.value.map {
                                    if (it.id == res.id) res else it
                                }
                            }
                            navController.popBackStack()
                        } else {
                            Log.e("API", "Data is null : $res")
                        }
                    }
                } else {
                    Log.e("API", "Response Failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun deleteSubscription(id: Int, navController: NavController) {
        viewModelScope.launch {
            try {
                val response = api.deleteSubscription(id)
                if (response.isSuccessful) {
                    response.body()?.let { res ->
                        if (res.data != null) {
                           withContext(Dispatchers.IO) {
                               _subscriptions.value = _subscriptions.value.filter {
                                   it.id != res.data.id
                               }
                           }
                            navController.popBackStack()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }
}