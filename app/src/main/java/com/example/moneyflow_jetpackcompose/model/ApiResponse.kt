package com.example.moneyflow_jetpackcompose.model

import com.google.gson.annotations.SerializedName


data class ApiResponse<T> (
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: T?
)