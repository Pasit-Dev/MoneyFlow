package com.example.moneyflow_jetpackcompose.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class CategoryModel (
   @SerializedName("id") val categoryId: Int,
   @SerializedName("name") val categoryName: String,
   @SerializedName("emoji") val emoji: String,
   @SerializedName("type") val type: String,
   @SerializedName("color") val color: String,
) : Parcelable