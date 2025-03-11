package com.example.moneyflow_jetpackcompose.api

import com.example.moneyflow_jetpackcompose.model.ApiResponse
import com.example.moneyflow_jetpackcompose.model.GoalModel
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class GoalRequest (
    @SerializedName("name") val name: String,
    @SerializedName("img") val img: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("target_amount") val targetAmount: Double,
    @SerializedName("current_amount") val currentAmount: Double,
    @SerializedName("user_id") val userId: String
)

interface GoalApi {
    @GET("goal/user/{userId}")
    suspend fun getGoal(@Path("userId") userId: String) : Response<ApiResponse<List<GoalModel>>>

    @GET("goal/{id}")
    suspend fun getGoalById(@Path("id") id: Int) : Response<ApiResponse<GoalModel>>

    @POST("goal")
    suspend fun createGoal(@Body goal: GoalRequest) : Response<ApiResponse<GoalModel>>

    @PUT("goal/{id}")
    suspend fun updateGaol(@Path("id") id: Int, @Body goal: GoalRequest) : Response<ApiResponse<GoalModel>>

    @DELETE("goal/{id}")
    suspend fun deleteGoal(@Path("id") id: Int) : Response<ApiResponse<GoalModel>>
}