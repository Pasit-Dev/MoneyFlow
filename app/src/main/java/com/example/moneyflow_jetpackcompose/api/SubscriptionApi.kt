package com.example.moneyflow_jetpackcompose.api

import com.example.moneyflow_jetpackcompose.model.ApiResponse
import com.example.moneyflow_jetpackcompose.model.SubscriptionModel
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


data class SubscriptionRequest (
    @SerializedName("note") val note: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("current_bill") val currentBill: String,
    @SerializedName("frequency") val frequency: String,
    @SerializedName("user_id") val userId: String,
)

interface SubscriptionApi {
    @GET("subscription/user/{userId}")
    suspend fun getSubscription(@Path("userId") userId: String) : Response<ApiResponse<List<SubscriptionModel>>>

    @GET("subscription/{id}")
    suspend fun getSubscriptionById(@Path("id") id: Int) : Response<ApiResponse<SubscriptionModel>>

    @POST("subscription")
    suspend fun createSubscription(@Body subscription: SubscriptionRequest) : Response<ApiResponse<SubscriptionModel>>

    @PUT("subscription/{id}")
    suspend fun updateSubscription(@Path("id") id: Int, @Body subscription: SubscriptionRequest) : Response<ApiResponse<SubscriptionModel>>

    @DELETE("subscription/{id}")
    suspend fun deleteSubscription(@Path("id") id: Int) : Response<ApiResponse<SubscriptionModel>>
}