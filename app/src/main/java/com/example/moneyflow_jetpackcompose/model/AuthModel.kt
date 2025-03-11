package com.example.moneyflow_jetpackcompose.model

import com.google.gson.annotations.SerializedName

data class AuthModel (
    @SerializedName("user_id") val userId: String,
    @SerializedName("email") val email: String,
    @SerializedName("imgprofile") val imgProfile: String,
)