package com.example.moneyflow_jetpackcompose.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubscriptionModel (
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("amount") val amount: Float,
    @SerializedName("current_bill") val currentBill: String,
    @SerializedName("frequency") val frequency: String,
) : Parcelable


