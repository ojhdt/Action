package com.ojhdtapp.action.logic.network

import android.util.Log
import androidx.core.content.ContextCompat
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.R
import com.ojhdtapp.action.logic.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Time
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Network {
    suspend fun getForecastResponse(lng: String, lat: String): ForecastResponse {
        val service = ServiceBuilder.build<WeatherService>("weather")
        return service.getForecastWeather(lng, lat).await()
    }

    suspend fun getLocationResponse(lng: String, lat: String): LocationResponse {
        val service = ServiceBuilder.build<LocationService>("location")
        return service.getLocation(location = "${lng},${lat}").await()
    }
}

private suspend fun <T> Call<T>.await(): T {
    return suspendCoroutine<T> { continuation ->
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                if (body != null)
                    continuation.resume(body) else
                    continuation.resumeWithException(RuntimeException("NULL"))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(RuntimeException("AwaitFailure"))
//                Log.d("aaa",t.localizedMessage)
            }
        })
    }
}
