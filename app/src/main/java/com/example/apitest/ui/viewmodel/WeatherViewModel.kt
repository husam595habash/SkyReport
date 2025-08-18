package com.example.apitest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.apitest.data.model.DailyWeather
import com.example.apitest.data.model.hourlyWeather
import com.example.apitest.data.repository.WeatherRepository
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _weatherList = MutableLiveData<List<hourlyWeather>?>()
    val weatherList: LiveData<List<hourlyWeather>?> = _weatherList

    private val _DailyWeatherList = MutableLiveData<List<DailyWeather>?>()
    val DailyWeatherList: LiveData<List<DailyWeather>?> = _DailyWeatherList

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchWeather() {
        viewModelScope.launch {
            val result = repository.getWeather()
            if (result != null) {
                val filtered = filterTodayFromNow(result)
                _weatherList.value = filtered
                _error.value = null
            } else {
                _weatherList.value = null
                _error.value = "Failed to load weather data"
            }
        }
    }

    fun fetchDailyWeather() {
        viewModelScope.launch {
            val result = repository.getDailyWeather()
            if (result != null) {
                _DailyWeatherList.value = result
                _error.value = null
            } else {
                _DailyWeatherList.value = null
                _error.value = "Failed to load daily weather data"
            }
        }
    }




    fun filterTodayFromNow(hourlyList: List<hourlyWeather>): List<hourlyWeather> {
        val now = ZonedDateTime.now(ZoneId.systemDefault())

        return hourlyList.filter { hourData ->
            val time = ZonedDateTime.parse(hourData.time).withZoneSameInstant(ZoneId.systemDefault())
            time.toLocalDate().isEqual(now.toLocalDate()) && time.hour >= now.hour
        }
    }

}
