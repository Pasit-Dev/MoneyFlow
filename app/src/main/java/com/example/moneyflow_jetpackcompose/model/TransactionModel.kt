package com.example.moneyflow_jetpackcompose.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionModel(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("date") val date: String,
    @SerializedName("type") val type: String,
    @SerializedName("account_id") val accountId: Int,
    @SerializedName("category_id") val categoryId: Int? = null,
    @SerializedName("goal_id") val goalId: Int? = null,
    @SerializedName("to_account_id") val toAccountId: Int? = null,
    @SerializedName("subscription_id") val subscriptionId: Int? = null,
    @SerializedName("emoji") val emoji: String? = "",
    @SerializedName("color") val color: String? = "",
    @SerializedName("category_name") val categoryName: String? = ""
) : Parcelable

