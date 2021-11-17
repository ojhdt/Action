package com.ojhdtapp.action.logic.network

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
        return ServiceBuilder.build<WeatherService>("weather").getForecastWeather(lng, lat).await()
    }

    suspend fun getLocationResponse(lng: String, lat: String): LocationResponse {
        return ServiceBuilder.build<LocationService>("location")
            .getLocation(location = "${lng},${lat}").await()
    }

    private fun getStringResource(id: Int): String {
        return BaseApplication.context.resources.getString(id)
    }

    suspend fun getWeather(): List<Any?> {
        val lng = "116.310003"
        val lat = "39.991957"
        val skyconMap = mapOf(
            "CLEAR_DAY" to getStringResource(R.string.weather_type_clear),
            "CLEAR_NIGHT" to getStringResource(R.string.weather_type_clear),
            "PARTLY_CLOUDY_DAY" to getStringResource(R.string.weather_type_partly_cloudy),
            "PARTLY_CLOUDY_NIGHT" to getStringResource(R.string.weather_type_partly_cloudy),
            "CLOUDY" to getStringResource(R.string.weather_type_cloudy),
            "LIGHT_HAZE" to getStringResource(R.string.weather_type_light_haze),
            "MODERATE_HAZE" to getStringResource(R.string.weather_type_moderate_haze),
            "HEAVY_HAZE" to getStringResource(R.string.weather_type_heavy_haze),
            "LIGHT_RAIN" to getStringResource(R.string.weather_type_light_rain),
            "MODERATE_RAIN" to getStringResource(R.string.weather_type_moderate_rain),
            "HEAVY_RAIN" to getStringResource(R.string.weather_type_heavy_rain),
            "STORM_RAIN" to getStringResource(R.string.weather_type_storm_rain),
            "FOG" to getStringResource(R.string.weather_type_fog),
            "LIGHT_SNOW" to getStringResource(R.string.weather_type_light_snow),
            "MODERATE_SNOW" to getStringResource(R.string.weather_type_moderate_snow),
            "HEAVY_SNOW" to getStringResource(R.string.weather_type_heavy_snow),
            "STORM_SNOW" to getStringResource(R.string.weather_type_storm_snow),
            "DUST" to getStringResource(R.string.weather_type_dust),
            "SAND" to getStringResource(R.string.weather_type_sand),
            "WIND" to getStringResource(R.string.weather_type_wind)
        )
        val rawMap = mapOf(
            "CLEAR_DAY" to R.raw.weather_sunny,
            "CLEAR_NIGHT" to R.raw.weather_night,
            "PARTLY_CLOUDY_DAY" to R.raw.weather_partly_cloudy,
            "PARTLY_CLOUDY_NIGHT" to R.raw.weather_loudynight,
            "CLOUDY" to R.raw.weather_partly_cloudy,
            "LIGHT_HAZE" to R.raw.weather_mist,
            "MODERATE_HAZE" to R.raw.weather_mist,
            "HEAVY_HAZE" to R.raw.weather_mist,
            "LIGHT_RAIN" to R.raw.weather_partly_shower,
            "MODERATE_RAIN" to R.raw.weather_partly_shower,
            "HEAVY_RAIN" to R.raw.weather_rainynight,
            "STORM_RAIN" to R.raw.weather_storm,
            "FOG" to R.raw.weather_foggy,
            "LIGHT_SNOW" to R.raw.weather_snow,
            "MODERATE_SNOW" to R.raw.weather_snow,
            "HEAVY_SNOW" to R.raw.weather_snow,
            "STORM_SNOW" to R.raw.weather_snow,
            "DUST" to R.raw.weather_mist,
            "SAND" to R.raw.weather_mist,
            "WIND" to R.raw.weather_windy
        )
        val systemCalendarHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val weatherService = ServiceBuilder.build<WeatherService>("weather")
        val locationService = ServiceBuilder.build<LocationService>("location")
        // Suspend
//        val realTimeResponse = weatherService.getRealtimeWeather(lng, lat).await()
        val forecastResponse = weatherService.getForecastWeather(lng, lat).await()
        val locationResponse = locationService.getLocation(location = "${lng},${lat}").await()
        forecastResponse.result.run {
            val weather = WeatherBlock(
                locationResponse.regeocode.formatted_address,
                skyconMap[realtime.skycon]!!,
                WeatherBlock.WeatherTemperature(
                    rawMap[realtime.skycon]!!,
                    realtime.temperature.toInt()
                ),
                WeatherBlock.WeatherTemperature(
                    rawMap[hourly.skycon[systemCalendarHour - 1].value]!!,
                    hourly.temperature[systemCalendarHour - 1].value.toInt()
                ),
                WeatherBlock.WeatherTemperature(
                    rawMap[hourly.skycon[systemCalendarHour].value]!!,
                    hourly.temperature[systemCalendarHour].value.toInt()
                ),
                WeatherBlock.WeatherTemperature(
                    rawMap[hourly.skycon[systemCalendarHour + 1].value]!!,
                    hourly.temperature[systemCalendarHour + 1].value.toInt()
                ),
                WeatherBlock.WeatherTemperature(
                    rawMap[hourly.skycon[systemCalendarHour + 2].value]!!,
                    hourly.temperature[systemCalendarHour + 2].value.toInt()
                ),
                WeatherBlock.WeatherTemperature(
                    rawMap[daily.skycon[systemCalendarHour - 1].value]!!,
                    daily.temperature[systemCalendarHour - 1].avg.toInt(),
                    daily.temperature[systemCalendarHour - 1].min.toInt(),
                    daily.temperature[systemCalendarHour - 1].max.toInt()
                ),
                WeatherBlock.WeatherTemperature(
                    rawMap[daily.skycon[systemCalendarHour].value]!!,
                    daily.temperature[systemCalendarHour].avg.toInt(),
                    daily.temperature[systemCalendarHour].min.toInt(),
                    daily.temperature[systemCalendarHour].max.toInt()
                )
            )
            val air = WeatherMessageBlock(
                R.drawable.ic_outline_air_24, getStringResource(R.string.air),
                realtime.air_quality.aqi.chn, realtime.air_quality.aqi.chn / 2
            )
            val life = LifeMessageBlock(
                realtime.life_index.ultraviolet.index.toInt(),
                realtime.life_index.ultraviolet.index.toInt() * 10,
                realtime.life_index.comfort.index,
                realtime.life_index.comfort.index * 10
            )
            return listOf(weather, air, life)
        }
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
                continuation.resumeWithException(RuntimeException("Failure"))
            }
        })
    }
}
