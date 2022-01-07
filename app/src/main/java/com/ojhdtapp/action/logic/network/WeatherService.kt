package com.ojhdtapp.action.logic.network

import com.ojhdtapp.action.logic.model.ForecastResponse
import com.ojhdtapp.action.logic.model.RealTimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {
    @GET("v2.5/Q0tltxXyl5JKD965/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Call<RealTimeResponse>

    @GET("v2.5/Q0tltxXyl5JKD965/{lng},{lat}/weather.json")
    fun getForecastWeather(
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Call<ForecastResponse>
}