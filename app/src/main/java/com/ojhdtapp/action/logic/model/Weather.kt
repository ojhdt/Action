package com.ojhdtapp.action.logic.model

data class Weather(
    val weather: WeatherBlock,
    val air: WeatherMessageBlock,
    val life: LifeMessageBlock
)