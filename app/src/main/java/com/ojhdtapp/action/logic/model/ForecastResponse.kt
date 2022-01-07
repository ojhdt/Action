package com.ojhdtapp.action.logic.model

data class ForecastResponse(
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
        val daily: Daily,
        val forecast_keypoint: String,
        val hourly: Hourly,
        val minutely: Minutely,
        val primary: Int,
        val realtime: Realtime
    ) {
        data class Daily(
            val air_quality: AirQuality,
            val astro: List<Astro>,
            val cloudrate: List<Cloudrate>,
            val dswrf: List<Dswrf>,
            val humidity: List<Humidity>,
            val life_index: LifeIndex,
            val precipitation: List<Precipitation>,
            val pressure: List<Pressure>,
            val skycon: List<Skycon>,
            val skycon_08h_20h: List<Skycon08h20h>,
            val skycon_20h_32h: List<Skycon20h32h>,
            val status: String,
            val temperature: List<Temperature>,
            val visibility: List<Visibility>,
            val wind: List<Wind>
        ) {
            data class AirQuality(
                val aqi: List<Aqi>,
                val pm25: List<Pm25>
            ) {
                data class Aqi(
                    val avg: Avg,
                    val date: String,
                    val max: Max,
                    val min: Min
                ) {
                    data class Avg(
                        val chn: Double,
                        val usa: Double
                    )

                    data class Max(
                        val chn: Int,
                        val usa: Int
                    )

                    data class Min(
                        val chn: Int,
                        val usa: Int
                    )
                }

                data class Pm25(
                    val avg: Double,
                    val date: String,
                    val max: Int,
                    val min: Int
                )
            }

            data class Astro(
                val date: String,
                val sunrise: Sunrise,
                val sunset: Sunset
            ) {
                data class Sunrise(
                    val time: String
                )

                data class Sunset(
                    val time: String
                )
            }

            data class Cloudrate(
                val avg: Double,
                val date: String,
                val max: Double,
                val min: Double
            )

            data class Dswrf(
                val avg: Double,
                val date: String,
                val max: Double,
                val min: Double
            )

            data class Humidity(
                val avg: Double,
                val date: String,
                val max: Double,
                val min: Double
            )

            data class LifeIndex(
                val carWashing: List<CarWashing>,
                val coldRisk: List<ColdRisk>,
                val comfort: List<Comfort>,
                val dressing: List<Dressing>,
                val ultraviolet: List<Ultraviolet>
            ) {
                data class CarWashing(
                    val date: String,
                    val desc: String,
                    val index: String
                )

                data class ColdRisk(
                    val date: String,
                    val desc: String,
                    val index: String
                )

                data class Comfort(
                    val date: String,
                    val desc: String,
                    val index: String
                )

                data class Dressing(
                    val date: String,
                    val desc: String,
                    val index: String
                )

                data class Ultraviolet(
                    val date: String,
                    val desc: String,
                    val index: String
                )
            }

            data class Precipitation(
                val avg: Double,
                val date: String,
                val max: Double,
                val min: Double
            )

            data class Pressure(
                val avg: Double,
                val date: String,
                val max: Double,
                val min: Double
            )

            data class Skycon(
                val date: String,
                val value: String
            )

            data class Skycon08h20h(
                val date: String,
                val value: String
            )

            data class Skycon20h32h(
                val date: String,
                val value: String
            )

            data class Temperature(
                val avg: Double,
                val date: String,
                val max: Double,
                val min: Double
            )

            data class Visibility(
                val avg: Double,
                val date: String,
                val max: Double,
                val min: Double
            )

            data class Wind(
                val avg: Avg,
                val date: String,
                val max: Max,
                val min: Min
            ) {
                data class Avg(
                    val direction: Double,
                    val speed: Double
                )

                data class Max(
                    val direction: Double,
                    val speed: Double
                )

                data class Min(
                    val direction: Double,
                    val speed: Double
                )
            }
        }

        data class Hourly(
            val air_quality: AirQuality,
            val cloudrate: List<Cloudrate>,
            val description: String,
            val dswrf: List<Dswrf>,
            val humidity: List<Humidity>,
            val precipitation: List<Precipitation>,
            val pressure: List<Pressure>,
            val skycon: List<Skycon>,
            val status: String,
            val temperature: List<Temperature>,
            val visibility: List<Visibility>,
            val wind: List<Wind>
        ) {
            data class AirQuality(
                val aqi: List<Aqi>,
                val pm25: List<Pm25>
            ) {
                data class Aqi(
                    val datetime: String,
                    val value: Value
                ) {
                    data class Value(
                        val chn: Int,
                        val usa: Int
                    )
                }

                data class Pm25(
                    val datetime: String,
                    val value: Int
                )
            }

            data class Cloudrate(
                val datetime: String,
                val value: Double
            )

            data class Dswrf(
                val datetime: String,
                val value: Double
            )

            data class Humidity(
                val datetime: String,
                val value: Double
            )

            data class Precipitation(
                val datetime: String,
                val value: Double
            )

            data class Pressure(
                val datetime: String,
                val value: Double
            )

            data class Skycon(
                val datetime: String,
                val value: String
            )

            data class Temperature(
                val datetime: String,
                val value: Double
            )

            data class Visibility(
                val datetime: String,
                val value: Double
            )

            data class Wind(
                val datetime: String,
                val direction: Double,
                val speed: Double
            )
        }

        data class Minutely(
            val datasource: String,
            val description: String,
            val precipitation: List<Double>,
            val precipitation_2h: List<Double>,
            val probability: List<Double>,
            val status: String
        )

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
                val local: Local,
                val nearest: Nearest
            ) {
                data class Local(
                    val datasource: String,
                    val intensity: Double,
                    val status: String
                )

                data class Nearest(
                    val distance: Double,
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