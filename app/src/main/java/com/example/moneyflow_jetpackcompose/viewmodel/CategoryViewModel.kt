package com.example.moneyflow_jetpackcompose.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyflow_jetpackcompose.api.ApiClient
import com.example.moneyflow_jetpackcompose.api.CategoryRequest
import com.example.moneyflow_jetpackcompose.datastore.DataStoreManager
import com.example.moneyflow_jetpackcompose.model.CategoryModel
import kotlinx.coroutines.launch


class CategoryViewModel : ViewModel() {
    private val api = ApiClient.categoryApi

    private val _categories = mutableStateOf<List<CategoryModel>>(emptyList())
    val categories : State<List<CategoryModel>> =  _categories

    fun fetchCategories(context: Context) {
        viewModelScope.launch {
            try {
                val userId = DataStoreManager.getToken(context)
                val response = api.getCategory(userId!!)
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        Log.d("API", "${it}")
                        if (it is List<CategoryModel>) {
                            _categories.value = it
                        }

                    }
                }
                Log.d("API", "Fetch Successful")
            } catch (e : Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun createCategory(context: Context, category: CategoryRequest) {
        viewModelScope.launch {
            try {

                val response = api.createCategory(category)
                Log.d("API", "${response.code()}")
                if (response.isSuccessful) {
                    response.body()?.data.let {
                        _categories.value += it!!
                    }
                    Log.d("API", "${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun updateCategory(context: Context,id: Int, category: CategoryRequest) {
        viewModelScope.launch {
            try {
                val userId = DataStoreManager.getToken(context)
                val response = api.updateCategory(id, category)
                if (response.isSuccessful) {
                    response.body()?.data.let {
                        _categories.value = _categories.value.map {
                            if (response.body()?.data?.categoryId == id) response.body()?.data!! else it
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun deleteCategory(context: Context, id: Int) {
        viewModelScope.launch {
            try {
                val response = api.deleteCategory(id)
                if (response.isSuccessful) {
                    response.body()?.let { res ->
                        _categories.value = _categories.value.filter { it.categoryId !=  res.data?.categoryId}
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.localizedMessage}")
            }
        }
    }
}