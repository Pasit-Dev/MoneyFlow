package com.example.moneyflow_jetpackcompose.api

import com.example.moneyflow_jetpackcompose.model.ApiResponse
import com.example.moneyflow_jetpackcompose.model.AuthModel
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class AuthRequest(
    val email: String,
    val password: String,
)

data class AuthResponse(
    val message: String,
    val token: String,
)

data class UpdateEmailRequest(val email: String)

data class UpdatePasswordRequest(val oldPassword: String, val newPassword: String)

data class ImageRequest(
    @SerializedName("image") val image: String,
)
interface AuthApi  {
    @POST("auth/register")
    suspend fun register(@Body requestBody: AuthRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body requestBody: AuthRequest): Response<AuthResponse>

    @GET("auth/{id}")
    suspend fun getUser(@Path("id") id: String) : Response<AuthModel>

    @PUT("auth/update-image/{id}")
    suspend fun updateImage(@Path("id") id: String, @Body image: ImageRequest) : Response<ApiResponse<AuthModel>>

    @PUT("auth/update-email/{id}")
    suspend fun updateEmail(@Path("id") id: String, @Body email: UpdateEmailRequest) : Response<ApiResponse<AuthModel>>

    @PUT("auth/update-password/{id}")
    suspend fun updatePassword(@Path("id") id: String, @Body password: UpdatePasswordRequest) : Response<ApiResponse<AuthModel>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body email: UpdateEmailRequest) : Response<ApiResponse<AuthModel>>
}