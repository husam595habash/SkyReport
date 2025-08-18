package com.example.apitest.data.repository

import android.util.Log
import com.example.apitest.data.api.WeatherApiServices
import com.example.apitest.data.model.DailyWeather
import com.example.apitest.data.model.hourlyWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository {

    suspend fun getWeather(): List<hourlyWeather>? = withContext(Dispatchers.IO) {
        try {
            WeatherApiServices.fetchWeatherData()
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Exception: ${e.message}", e)
            null
        }

    }

    suspend fun getDailyWeather(): List<DailyWeather>? = withContext(Dispatchers.IO) {
        try {
            WeatherApiServices.fetchDailyWeatherData()
        }catch (e: Exception) {
            Log.e("WeatherRepository", "Exception: ${e.message}", e)
            null
        }
    }
}
