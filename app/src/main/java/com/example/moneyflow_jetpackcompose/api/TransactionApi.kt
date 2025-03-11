package com.example.moneyflow_jetpackcompose.api

import com.example.moneyflow_jetpackcompose.model.ApiResponse
import com.example.moneyflow_jetpackcompose.model.TransactionModel
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


data class TransactionRequest (
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("date") val date: String,
    @SerializedName("type") val type: String,
    @SerializedName("account_id") val accountId: Int? = null,
    @SerializedName("category_id") val categoryId: Int? = null,
    @SerializedName("goal_id") val goalId: Int? = null,
    @SerializedName("to_account_id") val toAccountId: Int? = null,
    @SerializedName("subscription_id") val subscriptionId: Int? = null,
    @SerializedName("user_id") val userId: String,
)

interface TransactionApi {
    @GET("transaction/{userId}/month/{month}/{year}")
    suspend fun getTransactionByMonth(@Path("userId") userId: String, @Path("month") month: String, @Path("year") year: String) : Response<ApiResponse<List<TransactionModel>>>

    @GET("transaction/{userId}/year/{year}")
    suspend fun getTransactionByYear(@Path("userId") userId: String, @Path("year") year: String) : Response<ApiResponse<List<TransactionModel>>>

    @POST("transaction")
    suspend fun createTransaction(@Body transaction: TransactionRequest) : Response<ApiResponse<TransactionModel>>

    @PUT("transaction/{id}")
    suspend fun updateTransaction(@Path("id") id: Int, @Body transaction: TransactionRequest) : Response<ApiResponse<TransactionModel>>

    @DELETE("transaction/{id}")
    suspend fun deleteTransaction(@Path("id") id: Int) : Response<ApiResponse<TransactionModel>>
}