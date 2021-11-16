package com.ojhdtapp.action.ui.home

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
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
import com.ojhdtapp.action.logic.model.Weather
import com.ojhdtapp.action.logic.model.WeatherMessageBlock

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
                0 -> (holder as WeatherViewHolder).bind(item as Weather)
                1 -> (holder as AirViewHolder).bind(item as WeatherMessageBlock)
                else -> (holder as LifeViewHolder).bind(item as LifeMessageBlock)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }
    }

    class WeatherViewHolder(val binding: ExploreWeatherCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(weather: Weather) {
            binding.run {
                weatherLocation.text = weather.location
                weatherSkycon.text = weather.skycon
                weatherIconNow.setAnimation(weather.temperatureNow.rawID)
                weatherTemperatureNow.text = weather.temperatureNow.value.toString()
                weatherTemperatureHighest.text = weather.temperatureNow.highest.toString()
                weatherTemperatureLowest.text = weather.temperatureNow.lowest.toString()
                weatherIconNextHour.setAnimation(weather.temperature1HourLater.rawID)
                weatherTemperatureNextHour.text = weather.temperature1HourLater.value.toString()
                weatherIconNext2Hour.setAnimation(weather.temperature2HoursLater.rawID)
                weatherTemperatureNext2Hour.text = weather.temperature2HoursLater.value.toString()
                weatherIconNext3Hour.setAnimation(weather.temperature3HoursLater.rawID)
                weatherTemperatureNext3Hour.text = weather.temperature3HoursLater.value.toString()
                weatherIconNext4Hour.setAnimation(weather.temperature4HoursLater.rawID)
                weatherTemperatureNext4Hour.text = weather.temperature4HoursLater.value.toString()
                weatherIconTomorrow.setAnimation(weather.temperatureTomorrow.rawID)
                weatherTemperatureTomorrowLowest.text =
                    weather.temperatureTomorrow.lowest.toString()
                weatherTemperatureTomorrowHighest.text =
                    weather.temperatureTomorrow.highest.toString()
                weatherIconTheDayAfterTomorrow.setAnimation(weather.temperatureTheDayAfterTomorrow.rawID)
                weatherTemperatureTheDayAfterTomorrowLowest.text =
                    weather.temperatureTheDayAfterTomorrow.lowest.toString()
                weatherTemperatureTheDayAfterTomorrowHighest.text =
                    weather.temperatureTheDayAfterTomorrow.highest.toString()
            }
        }
    }

    class AirViewHolder(val binding: ExploreAirQualityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: WeatherMessageBlock) {
            binding.run {
                Glide.with(root)
                    .load(data.drawableID)
                    .into(airIcon)
                airTitle.text = data.title
                airNum.text = data.num.toString()
                airProgressView.setProgress(data.progress)
            }
        }
    }

    class AirErrorViewHolder(val binding: ExploreAirQualityErrorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: WeatherMessageBlock) {
            binding.run {
                Glide.with(root)
                    .load(data.drawableID)
                    .into(airIcon)
                airTitle.text = data.title
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