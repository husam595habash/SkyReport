package com.example.apitest.ui.activity

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apitest.R
import com.example.apitest.ui.adapter.DailyWeatherAdapter
import com.example.apitest.ui.viewmodel.WeatherViewModel
import com.example.testone.News.hourlyWeatherAdapter

class Weather : AppCompatActivity() {

    private lateinit var hourlyAdapter: hourlyWeatherAdapter
    private lateinit var hourlyRV: RecyclerView

    private lateinit var dailyAdapter: DailyWeatherAdapter
    private lateinit var dailyRV: RecyclerView

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather)

        // Set up RecyclerView
        hourlyRV = findViewById(R.id.hourlyRV)
        hourlyRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        hourlyAdapter = hourlyWeatherAdapter(arrayListOf()) // start with empty list
        hourlyRV.adapter = hourlyAdapter

        val temp: TextView = findViewById(R.id.tempText)

        viewModel.weatherList.observe(this, Observer { weatherList ->
            if (weatherList != null) {
                hourlyAdapter = hourlyWeatherAdapter(ArrayList(weatherList))
                hourlyRV.adapter = hourlyAdapter
                temp.text = weatherList[0].temperature.toString()+"°"
            }
        })


        // Observe error state
        viewModel.error.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        // Fetch weather
        viewModel.fetchWeather()



        dailyRV = findViewById(R.id.dailyRV)
        dailyRV.layoutManager = LinearLayoutManager(this)
        dailyAdapter = DailyWeatherAdapter(arrayListOf())
        dailyRV.adapter = dailyAdapter

        viewModel.DailyWeatherList.observe(this) { weatherList ->
            if (weatherList != null) {
                dailyAdapter = DailyWeatherAdapter(ArrayList(weatherList))
                dailyRV.adapter = dailyAdapter
            }
        }

        viewModel.fetchDailyWeather()


    }
}
