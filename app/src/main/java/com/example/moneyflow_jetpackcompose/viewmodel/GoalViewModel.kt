package com.example.moneyflow_jetpackcompose.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moneyflow_jetpackcompose.api.ApiClient
import com.example.moneyflow_jetpackcompose.api.GoalRequest
import com.example.moneyflow_jetpackcompose.model.GoalModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.Async.Execute


class GoalViewModel: ViewModel() {
    private val api = ApiClient.goalApi

    private val _goals = mutableStateOf<List<GoalModel>>(emptyList())
    val goals: State<List<GoalModel>> = _goals

    private val _goal = mutableStateOf<GoalModel?>(null)
    val goal: State<GoalModel?> = _goal

    fun fetchGoal(userId: String) {
        viewModelScope.launch {
            try {
                val response = api.getGoal(userId)
                if (response.isSuccessful) {
                    response.body()?.let { res ->
                        if (res.data != null) {
                            _goals.value = res.data
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun fetchGoalById(id: Int) {
        viewModelScope.launch {
            try {
                val response = api.getGoalById(id)
                if (response.isSuccessful) {
                    response.body()?.let { res ->
                        if (res.data != null) {
                            _goal.value = res.data
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun createGoal(newGoal: GoalRequest, navController: NavController) {
        viewModelScope.launch {
            try {
                val response = api.createGoal(newGoal)
                if( response.isSuccessful) {
                    response.body()?.data?.let {
                        withContext(Dispatchers.IO) {
                            _goals.value += it
                        }
                        navController.popBackStack()
                    }
                } else {
                    Log.e("API", "Error response: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun updateGoal(id: Int, newGoal: GoalRequest) {
        viewModelScope.launch {
            try {
                val response = api.updateGaol(id, newGoal)
                if (response.isSuccessful) {
                    response.body()?.let { res ->
                        if (res.data != null) {
                            if (_goal.value?.id == id) {
                                _goal.value = res.data
                            }
                            _goals.value = _goals.value.map {
                                if (it.id == id) res.data else it
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch {
            try {
                val response = api.deleteGoal(id)
                if (response.isSuccessful) {
                    response.body()?.let { res ->
                        if (_goal.value?.id == res.data?.id) {
                            _goal.value = null
                        }
                        _goals.value = _goals.value.filter { it.id != res.data?.id }
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }
}