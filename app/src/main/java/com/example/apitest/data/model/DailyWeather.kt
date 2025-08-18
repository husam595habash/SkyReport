package com.example.apitest.data.model

data class DailyWeather(
    val weatherIconResId: Int,
    val dayOfWeek: String,
    val maxTemp: Int,
    val minTemp: Int
)
