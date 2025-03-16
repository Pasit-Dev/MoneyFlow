package com.example.moneyflow_jetpackcompose.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
//    private  const val BASE_URL = "http://localhost:3000/"
//    private const val BASE_URL = "http://10.0.2.2:3000" // android emulator
    private const val BASE_URL = "http://10.0.3.2:4000" // genymotion emulator

    val gson: Gson? = GsonBuilder().serializeNulls().create()
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val subscriptionApi: SubscriptionApi by lazy {
        retrofit.create(SubscriptionApi::class.java)
    }

    val goalApi: GoalApi by lazy {
        retrofit.create(GoalApi::class.java)
    }

    val accountApi: AccountApi by lazy {
        retrofit.create(AccountApi::class.java)
    }

    val categoryApi: CategoryApi by lazy {
        retrofit.create(CategoryApi::class.java)
    }

    val transactionApi: TransactionApi by lazy {
        retrofit.create(TransactionApi::class.java)
    }
}