package com.example.moneyflow_jetpackcompose.model

import com.google.gson.annotations.SerializedName


data class AccountModel (
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("balance") val balance: Double,
)