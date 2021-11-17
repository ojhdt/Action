package com.ojhdtapp.action.ui.home

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.ExploreAirQualityBinding
import com.ojhdtapp.action.databinding.ExploreAirQualityErrorBinding
import com.ojhdtapp.action.databinding.ExploreLifeBinding
import com.ojhdtapp.action.databinding.ExploreWeatherCardBinding
import com.ojhdtapp.action.logic.model.LifeMessageBlock
import com.ojhdtapp.action.logic.model.WeatherBlock
import com.ojhdtapp.action.logic.model.WeatherMessageBlock
import java.util.*

object ExploreAdapters {
    class WeatherAdapter :
        ListAdapter<Any?, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<Any?>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                return false
            }
        }) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            when (viewType) {
                0 -> {
                    val binding = ExploreWeatherCardBinding.inflate(layoutInflater, parent, false)
                    return WeatherViewHolder(binding)
                }
                1 -> {
                    if ((getItem(1) as WeatherMessageBlock).progress > 80) {
                        val binding =
                            ExploreAirQualityErrorBinding.inflate(layoutInflater, parent, false)
                        return AirErrorViewHolder(binding)
                    } else {
                        val binding =
                            ExploreAirQualityBinding.inflate(layoutInflater, parent, false)
                        return AirViewHolder(binding)
                    }
                }
                else -> {
                    val binding = ExploreLifeBinding.inflate(layoutInflater, parent, false)
                    return LifeViewHolder(binding)
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = getItem(position)
            when (holder.itemViewType) {
                0 -> (holder as WeatherViewHolder).bind(item as WeatherBlock)
                1 -> {
                    if ((item as WeatherMessageBlock).progress > 80) {
                        (holder as AirErrorViewHolder).bind(item)
                    } else {
                        (holder as AirViewHolder).bind(item)
                    }
                }

                else -> (holder as LifeViewHolder).bind(item as LifeMessageBlock)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }
    }

    class WeatherViewHolder(val binding: ExploreWeatherCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val systemCalendarHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        private fun getStringResource(id: Int, arg: String): String {
            return binding.root.resources.getString(id, arg)
        }

        fun bind(weatherBlock: WeatherBlock) {
            binding.run {
                weatherLocation.text = weatherBlock.location
                weatherSkycon.text = weatherBlock.skycon
                weatherIconNow.setAnimation(weatherBlock.temperatureNow.rawID)
                weatherTemperatureNow.text = getStringResource(
                    R.string.weather_temperature_value,
                    weatherBlock.temperatureNow.value.toString()
                )
//                weatherTemperatureHighest.text = weatherBlock.temperatureNow.highest.toString()
//                weatherTemperatureLowest.text = weatherBlock.temperatureNow.lowest.toString()
                weatherIconNextHour.setAnimation(weatherBlock.temperature1HourLater.rawID)
                weatherTemperatureNextHour.text = getStringResource(
                    R.string.weather_temperature_value,
                    weatherBlock.temperature1HourLater.value.toString()
                )
                weatherTimeNextHour.text = getStringResource(
                    R.string.weather_time_value,
                    ((systemCalendarHour + 1) % 24).toString()
                )
                weatherIconNext2Hour.setAnimation(weatherBlock.temperature2HoursLater.rawID)
                weatherTemperatureNext2Hour.text = getStringResource(
                    R.string.weather_temperature_value,
                    weatherBlock.temperature2HoursLater.value.toString()
                )
                weatherTimeNext2Hour.text = getStringResource(
                    R.string.weather_time_value,
                    ((systemCalendarHour + 2) % 24).toString()
                )
                weatherIconNext3Hour.setAnimation(weatherBlock.temperature3HoursLater.rawID)
                weatherTemperatureNext3Hour.text = getStringResource(
                    R.string.weather_temperature_value,
                    weatherBlock.temperature3HoursLater.value.toString()
                )
                weatherTimeNext3Hour.text = getStringResource(
                    R.string.weather_time_value,
                    ((systemCalendarHour + 3) % 24).toString()
                )
                weatherIconNext4Hour.setAnimation(weatherBlock.temperature4HoursLater.rawID)
                weatherTemperatureNext4Hour.text = getStringResource(
                    R.string.weather_temperature_value,
                    weatherBlock.temperature4HoursLater.value.toString()
                )
                weatherTimeNext4Hour.text = getStringResource(
                    R.string.weather_time_value,
                    ((systemCalendarHour + 4) % 24).toString()
                )
                weatherIconTomorrow.setAnimation(weatherBlock.temperatureTomorrow.rawID)
                weatherTemperatureTomorrowLowest.text = getStringResource(
                    R.string.weather_temperature_value,
                    weatherBlock.temperatureTomorrow.lowest.toString()
                )
                weatherTemperatureTomorrowHighest.text =
                    getStringResource(
                        R.string.weather_temperature_value,
                        weatherBlock.temperatureTomorrow.highest.toString()
                    )
                weatherIconTheDayAfterTomorrow.setAnimation(weatherBlock.temperatureTheDayAfterTomorrow.rawID)
                weatherTemperatureTheDayAfterTomorrowLowest.text = getStringResource(
                    R.string.weather_temperature_value,
                    weatherBlock.temperatureTheDayAfterTomorrow.lowest.toString()
                )
                weatherTemperatureTheDayAfterTomorrowHighest.text = getStringResource(
                    R.string.weather_temperature_value,
                    weatherBlock.temperatureTheDayAfterTomorrow.highest.toString()
                )
            }
        }
    }

    class AirViewHolder(val binding: ExploreAirQualityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: WeatherMessageBlock) {
            binding.run {
//                Glide.with(root)
//                    .load(data.drawableID)
//                    .into(airIcon)
//                airTitle.text = data.title
                airNum.text = data.num.toString()
                airProgressView.setProgress(data.progress)
            }
        }
    }

    class AirErrorViewHolder(val binding: ExploreAirQualityErrorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: WeatherMessageBlock) {
            binding.run {
//                Glide.with(root)
//                    .load(data.drawableID)
//                    .into(airIcon)
//                airTitle.text = data.title
                airNum.text = data.num.toString()
                airProgressView.setProgress(data.progress)
            }
        }
    }

    class LifeViewHolder(val binding: ExploreLifeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LifeMessageBlock) {
            binding.run {
                ValueAnimator.ofInt(0, data.ultravioletValue).apply {
                    duration = 1000
                    addUpdateListener {
                        ultravioletNum.text = (it.animatedValue as Int).toString()
                    }
                    start()
                }
                ObjectAnimator.ofInt(
                    ultravioletProcessView,
                    "progress",
                    0,
                    data.ultravioletProgress
                ).apply {
                    duration = 1000
                    start()
                }
                ValueAnimator.ofInt(0, data.ComfortValue).apply {
                    duration = 1000
                    addUpdateListener {
                        comfortNum.text = (it.animatedValue as Int).toString()
                    }
                    start()
                }
                ObjectAnimator.ofInt(comfortProcessView, "progress", 0, data.ComfortProgress)
                    .apply {
                        duration = 1000
                        start()
                    }
//                ultravioletNum.text = ultravioletData.num.toString()
//                ultravioletProcessView.setProgress(ultravioletData.progress)
//                comfortNum.text = comfortData.num.toString()
//                comfortProcessView.setProgress(comfortData.progress)
            }
        }
    }
}