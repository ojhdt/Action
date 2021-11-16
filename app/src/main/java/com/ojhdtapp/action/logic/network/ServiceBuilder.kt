package com.ojhdtapp.action.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private val weatherRetrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.caiyunapp.com/")
        .build()
    private val locationRetrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://restapi.amap.com/")
        .build()

    fun <T> build(serviceClass: Class<T>, type: String): T {
        return when (type) {
            "weather" -> weatherRetrofit.create(serviceClass)
            "location" -> locationRetrofit.create(serviceClass)
            else -> weatherRetrofit.create(serviceClass)
        }
    }

    inline fun <reified T> build(type: String): T {
        return build(T::class.java, type)
    }
}