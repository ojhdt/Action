package com.ojhdtapp.action.logic

import android.app.Application
import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.preference.PreferenceManager
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.Event
import com.ojhdtapp.action.R
import com.ojhdtapp.action.logic.model.*
import com.ojhdtapp.action.logic.network.Network
import com.ojhdtapp.action.util.LocationUtil
import com.ojhdtapp.action.util.NotificationUtil
import kotlinx.coroutines.*
import java.lang.RuntimeException
import java.math.RoundingMode
import java.sql.Timestamp
import java.text.DecimalFormat
import java.util.*

object Repository {
    private val database = AppDataBase.getDataBase()
    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(BaseApplication.context)
    }

    private fun getStringResource(id: Int): String {
        return BaseApplication.context.resources.getString(id)
    }

    private val skyconMap = mapOf(
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
        "WIND" to getStringResource(R.string.weather_type_wind),
    )
    private val rawMap = mapOf(
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

    fun getActionNowLive() = database.actionDao().loadAllActivatingActionLive()

    fun getSuggestMoreLive() = database.suggestDao().loadAllDisplaySuggestLive()

    fun getAllActionLive(): LiveData<List<Action>> =
        database.actionDao().loadAllActionLive()

    fun getActivatedActionLive(): LiveData<List<Action>> =
        database.actionDao().loadActivatedActionLive()

    fun getReadSuggestLive(): LiveData<List<Suggest>> =
        database.suggestDao().loadAllReadSuggestLive()

    fun getArchivedSuggestLive(): LiveData<List<Suggest>> =
        database.suggestDao().loadAllArchivedSuggestLive()

    fun getGainedAchievementLive(): LiveData<List<Achievement>> {
        return liveData {
            val list = listOf(
                Achievement(),
                Achievement(),
                Achievement(),
            )
            emit(list)
        }
    }

    fun loadAvailableActionByConditions(
        activityStateTrigger: Int = -1,
        lightStateTrigger: Int = -1,
        locationStateTrigger: Int = -1,
        timeStateTrigger: Int = -1,
        weatherStateTrigger: Int = -1,
        currentTime: Long = System.currentTimeMillis()
    ): List<Action> = database.actionDao().loadAvailableActionByConditions(
        activityStateTrigger,
        lightStateTrigger,
        locationStateTrigger,
        timeStateTrigger,
        weatherStateTrigger,
        currentTime
    )

    fun updateAllActivatingActionState() {
        val job = Job()
        CoroutineScope(job).launch {
            val list = database.actionDao().loadAllActivatingAction()
            val milltime = (sharedPreference.getInt("action_expired_time", 10) * 60000).toLong()
            if (list.isNotEmpty()) {
                list.forEach {
                    if (System.currentTimeMillis() - it.lastTriggered > milltime) {
                        it.let {
                            it.isActivating = false
                            Log.d(
                                "aaa",
                                "No." + it.id.toString() + "task has been automatically cleared"
                            )
                            database.actionDao().updateAction(it)
                            NotificationUtil.cancelAction(it.id.toInt())
                        }
                    }
                }
            }
        }
        job.complete()
    }

    // From Cloud
    suspend fun storeSuggestFromCloud(type: Int): Result<String> {
        return try {
            val result = LeanCloudDataBase.getNewSuggest(type)
            database.suggestDao().insertSuggest(result)
            Result.success(getStringResource(R.string.network_success))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Weather
    fun createTempWeatherLive(): LiveData<Result<Weather>> = liveData(Dispatchers.Main) {
        val weather = WeatherBlock(
            BaseApplication.context.getString(R.string.loading_location),
            BaseApplication.context.getString(R.string.loading_data),
            WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
            WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
            WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
            WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
            WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
            WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
            WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0)
        )
        val air = WeatherMessageBlock(
            R.drawable.ic_outline_air_24, BaseApplication.context.getString(R.string.air),
            0, 0
        )
        val life = LifeMessageBlock(0, 0, 0, 0)
        val result = Result.success(Weather(weather, air, life, true))
        emit(result)
    }

    fun getWeatherLive(): LiveData<Result<Weather>> = liveData(Dispatchers.IO) {
        var lng: String
        var lat: String
        val defaultLng = 116.310003
        val defaultLat = 39.991957
        val df = DecimalFormat("0.000000").apply {
            roundingMode = RoundingMode.HALF_UP
        }
        if (sharedPreference.getBoolean("locate", true)) {
            var currentLocation = LocationUtil.getLocation().also {
                if (it == null) {
                    emit(Result.failure(java.lang.Exception("Location Null")))
                }
            }
//        currentLocation = null
            lng = df.format(currentLocation?.longitude ?: defaultLng).trim()
            lat = df.format(currentLocation?.latitude ?: defaultLat).trim()
//            sharedPreference.edit()
//                .putString("lng", lng)
//                .putString("lat", lat)
//                .apply()
        } else {
            lng = sharedPreference.getString("lng", df.format(defaultLng).trim())!!
            lat = sharedPreference.getString("lat", df.format(defaultLat).trim())!!
        }

        val result = try {
            coroutineScope<Result<Weather>> {
                val forecastResponseJob = async {
                    Network.getForecastResponse(lng, lat)
                }
                val locationResponseJob = async {
                    Network.getLocationResponse(lng, lat)
                }
                val forecastResponse = forecastResponseJob.await()
                val locationResponse = locationResponseJob.await()
                if (forecastResponse.status == "ok" && locationResponse.status == "1") {
                    // pois
//                    Log.d("aaa", locationResponse.regeocode.pois.toString())
                    //weather block
                    val systemCalendarHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    forecastResponse.result.run {
                        val weather = WeatherBlock(
                            locationResponse.regeocode.formatted_address,
                            skyconMap[realtime.skycon]!!,
                            WeatherBlock.WeatherTemperature(
                                rawMap[realtime.skycon]!!,
                                realtime.temperature.toInt()
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
                                rawMap[hourly.skycon[systemCalendarHour + 3].value]!!,
                                hourly.temperature[systemCalendarHour + 3].value.toInt()
                            ),
                            WeatherBlock.WeatherTemperature(
                                rawMap[daily.skycon[0].value]!!,
                                daily.temperature[0].avg.toInt(),
                                daily.temperature[0].min.toInt(),
                                daily.temperature[0].max.toInt()
                            ),
                            WeatherBlock.WeatherTemperature(
                                rawMap[daily.skycon[1].value]!!,
                                daily.temperature[1].avg.toInt(),
                                daily.temperature[1].min.toInt(),
                                daily.temperature[1].max.toInt()
                            )
                        )
                        val air = WeatherMessageBlock(
                            R.drawable.ic_outline_air_24, getStringResource(R.string.air),
                            realtime.air_quality.aqi.chn, realtime.air_quality.aqi.chn / 4
                        )
                        val life = LifeMessageBlock(
                            realtime.life_index.ultraviolet.index.toInt(),
                            realtime.life_index.ultraviolet.index.toInt() * 10,
                            realtime.life_index.comfort.index,
                            realtime.life_index.comfort.index * 6
                        )
                        Result.success(Weather(weather, air, life))
                    }
                } else {
                    Result.failure(RuntimeException("Status:${forecastResponse.status},${locationResponse.status}"))
                }
            }
        } catch (e: Exception) {
            Log.d("aaa", e.toString())
            Result.failure(e)
        }
        emit(result)
    }
}