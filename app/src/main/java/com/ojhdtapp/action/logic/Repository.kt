package com.ojhdtapp.action.logic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.R
import com.ojhdtapp.action.logic.model.*
import com.ojhdtapp.action.logic.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.RuntimeException
import java.sql.Timestamp
import java.util.*

object Repository {
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

    fun getActionNowLive() = liveData {
        val list = listOf(
            Action(
                "McCarthy blasts Democrats, stalls Biden bill in over-8-hour tirade on House floor",
                R.drawable.city,
                getStringResource(R.string.lorem_ipsum),
                "位置信息",
                Timestamp(1636594566),
                listOf(
                    Pair(R.drawable.ic_outline_emoji_events_24, "WaterSave"),
                    Pair(R.drawable.ic_outline_emoji_events_24, "WaterSave")
                ),
                listOf("第一条","第二条","第三条")
            ),
            Action(
                "勤关水龙头",
                R.drawable.anonymous,
                "一些内容",
                "位置信息",
                Timestamp(1636594566),
                listOf(Pair(R.drawable.ic_outline_emoji_events_24, "WaterSave"))
            )
        )
        emit(list)
    }

    fun getSuggestMoreLive() = liveData {
        val list = listOf(
            Suggest(
                imgUrl = "https://tva2.sinaimg.cn/large/0072Vf1pgy1fodqgiodg1j31gs1191im.jpg",
                timestamp = Timestamp(1636594566),
                content = BaseApplication.context.getString(R.string.lorem_ipsum)
            )
        )
        emit(list)
    }

    fun getUserInfoLive(): LiveData<User> {
        return liveData {
            emit(User("Username", R.drawable.anonymous, Timestamp(0)))
        }
    }

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

    fun getFinishedActionLive(): LiveData<List<Action>> {
        return liveData {
            val list = listOf<Action>(
                Action(finished = true),
                Action(finished = true),
                Action(finished = true)
            )
            emit(list)
        }
    }

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
        val lng = "116.310003"
        val lat = "39.991957"
        val result = try {
            coroutineScope {
                Log.d("aaa", "GetDataFromNetwork")
                val forecastResponseJob = async {
                    Network.getForecastResponse(lng, lat)
                }
                val locationResponseJob = async {
                    Network.getLocationResponse(lng, lat)
                }
                val forecastResponse = forecastResponseJob.await()
                val locationResponse = locationResponseJob.await()
                if (forecastResponse.status == "ok" && locationResponse.status == "1") {
                    val systemCalendarHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    Log.d("aaa", systemCalendarHour.toString())
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
                            realtime.life_index.comfort.index * 8
                        )
                        Result.success(Weather(weather, air, life))
                    }
                } else {
                    Result.failure(RuntimeException("Status:${forecastResponse.status},${locationResponse.status}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }
}