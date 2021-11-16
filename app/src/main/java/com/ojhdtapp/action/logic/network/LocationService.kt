package com.ojhdtapp.action.logic.network

import com.ojhdtapp.action.logic.model.LocationResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationService {
    @GET("v3/geocode/regeo")
    fun getLocation(
        @Query("output") output: String = "json",
        @Query("location") location: String,
        @Query("key") key: String = "155c77c4b25804f153c398e3d6b75d25",
        @Query("radius") radius: String = "1000",
        @Query("extensions") extensions: String = "base"
    ): Call<LocationResponse>
}