package com.example.moneyflow_jetpackcompose.api

import com.example.moneyflow_jetpackcompose.model.AccountModel
import com.example.moneyflow_jetpackcompose.model.ApiResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class AccountRequest (
    @SerializedName("accountName") val accountName: String,
    @SerializedName("balance") val balance: Double,
    @SerializedName("userId") val userId: String? = null ,
)

interface AccountApi {
    @GET("account/user/{userId}")
    suspend fun getAccount(@Path("userId") userId: String): Response<ApiResponse<List<AccountModel>>>

    @GET("account/{id}")
    suspend fun getAccountById(@Path("id") id: Int): Response<ApiResponse<AccountModel>>

    @POST("account")
    suspend fun createAccount(@Body account: AccountRequest) : Response<ApiResponse<AccountModel>>

    @PUT("account/{id}")
    suspend fun updateAccount(@Path("id") id: Int, @Body account: AccountRequest) : Response<ApiResponse<AccountModel>>

    @DELETE("account/{id}")
    suspend fun deleteAccount(@Path("id") id: Int): Response<ApiResponse<AccountModel>>

}