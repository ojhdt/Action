package com.ojhdtapp.action.logic.model

data class RealTimeResponse(
    val api_status: String,
    val api_version: String,
    val lang: String,
    val location: List<Double>,
    val result: Result,
    val server_time: Int,
    val status: String,
    val timezone: String,
    val tzshift: Int,
    val unit: String
) {
    data class Result(
        val primary: Int,
        val realtime: Realtime
    ) {
        data class Realtime(
            val air_quality: AirQuality,
            val apparent_temperature: Double,
            val cloudrate: Double,
            val dswrf: Double,
            val humidity: Double,
            val life_index: LifeIndex,
            val precipitation: Precipitation,
            val pressure: Double,
            val skycon: String,
            val status: String,
            val temperature: Double,
            val visibility: Double,
            val wind: Wind
        ) {
            data class AirQuality(
                val aqi: Aqi,
                val co: Double,
                val description: Description,
                val no2: Double,
                val o3: Double,
                val pm10: Double,
                val pm25: Double,
                val so2: Double
            ) {
                data class Aqi(
                    val chn: Int,
                    val usa: Int
                )

                data class Description(
                    val chn: String,
                    val usa: String
                )
            }

            data class LifeIndex(
                val comfort: Comfort,
                val ultraviolet: Ultraviolet
            ) {
                data class Comfort(
                    val desc: String,
                    val index: Int
                )

                data class Ultraviolet(
                    val desc: String,
                    val index: Double
                )
            }

            data class Precipitation(
                val local: Local
            ) {
                data class Local(
                    val datasource: String,
                    val intensity: Double,
                    val status: String
                )
            }

            data class Wind(
                val direction: Double,
                val speed: Double
            )
        }
    }
}