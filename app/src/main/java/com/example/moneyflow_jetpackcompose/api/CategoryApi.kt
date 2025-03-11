package com.example.moneyflow_jetpackcompose.api

import androidx.datastore.preferences.protobuf.Api
import com.example.moneyflow_jetpackcompose.model.ApiResponse
import com.example.moneyflow_jetpackcompose.model.CategoryModel
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class CategoryRequest(
    @SerializedName("categoryName")  val categoryName: String,
    @SerializedName("emoji") val emoji: String,
    @SerializedName("type") val type: String,
    @SerializedName("color") val color: String,
    @SerializedName("userId") val userId: String? = null
)

interface CategoryApi {
    @GET("category/user/{userId}")
    suspend fun getCategory(@Path("userId") userId: String) : Response<ApiResponse<List<CategoryModel>>>

    @GET("category/{id}/")
    suspend fun getCategoryById(@Path("id") id: Int) : Response<ApiResponse<CategoryModel>>

    @POST("category")
    suspend fun createCategory(@Body request: CategoryRequest) : Response<ApiResponse<CategoryModel>>

    @PUT("category/{id}")
    suspend fun updateCategory(@Path("id") id: Int, @Body request: CategoryRequest) : Response<ApiResponse<CategoryModel>>

    @DELETE("category/{id}")
    suspend fun deleteCategory(@Path("id") id: Int) : Response<ApiResponse<CategoryModel>>
}