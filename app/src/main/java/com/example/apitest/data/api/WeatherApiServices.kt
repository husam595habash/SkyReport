package com.example.apitest.data.api

import android.util.Log
import com.example.apitest.data.model.DailyWeather
import com.example.apitest.data.model.hourlyWeather
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import kotlin.math.round

class WeatherApiServices {

    companion object {
        private val client = OkHttpClient()

        // Hourly Weather Fetch
        @Throws(IOException::class)
        fun fetchWeatherData(): List<hourlyWeather>? {
            val request = Request.Builder()
                .url("https://api.tomorrow.io/v4/weather/forecast?location=42.3478,-71.0466&timesteps=1h&fields=temperature,weatherCode&apikey=d3sb69U3QENWoorvKfgni40JzJunq5lg")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                Log.e("API_ERROR", "HTTP ${response.code}: ${response.message}")
                return null
            }

            return try {
                Log.d("API_RESPONSE", responseBody)

                val jsonObject = JSONObject(responseBody)
                val timelinesObject = jsonObject.getJSONObject("timelines")
                val hourlyArray = timelinesObject.getJSONArray("hourly")

                val weatherInfoList = mutableListOf<hourlyWeather>()

                for (i in 0 until hourlyArray.length()) {
                    val hourlyObj = hourlyArray.getJSONObject(i)
                    val time = hourlyObj.getString("time")

                    val valuesObj = hourlyObj.getJSONObject("values")
                    val temperature = round(valuesObj.optDouble("temperature", Double.NaN)).toInt()
                    val weatherCode = valuesObj.optInt("weatherCode", -1)

                    if (weatherCode != -1) {
                        weatherInfoList.add(hourlyWeather(time, temperature, weatherCode))
                    }
                }

                Log.d("API_RESPONSE", "Hourly count: ${weatherInfoList.size}")
                weatherInfoList

            } catch (e: Exception) {
                Log.e("API_PARSE_ERROR", "JSON Parsing failed: ${e.message}")
                e.printStackTrace()
                null
            }
        }

        @Throws(IOException::class)
        fun fetchDailyWeatherData(): List<DailyWeather>? {
            val dailyUrl = "https://api.tomorrow.io/v4/weather/forecast" +
                    "?location=42.3478,-71.0466" +
                    "&timesteps=1d" +
                    "&fields=temperatureMax,temperatureMin,weatherCodeMax" +
                    "&apikey=d3sb69U3QENWoorvKfgni40JzJunq5lg"

            val request = Request.Builder()
                .url(dailyUrl)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                Log.e("API_ERROR", "HTTP ${response.code}: ${response.message}")
                return null
            }

            return try {
                val jsonObject = JSONObject(responseBody)
                val timelinesObject = jsonObject.getJSONObject("timelines")
                val dailyArray = timelinesObject.getJSONArray("daily")

                val dailyWeatherList = mutableListOf<DailyWeather>()

                for (i in 0 until dailyArray.length()) {
                    val dailyObj = dailyArray.getJSONObject(i)
                    val time = dailyObj.getString("time")

                    val valuesObj = dailyObj.getJSONObject("values")
                    val tempMaxRaw = valuesObj.optDouble("temperatureMax", Double.NaN)
                    val tempMinRaw = valuesObj.optDouble("temperatureMin", Double.NaN)
                    val weatherCode = valuesObj.optInt("weatherCodeMax", -1)

                    // Only add if temps are valid and weather code is valid
                    if (!tempMaxRaw.isNaN() && !tempMinRaw.isNaN() && weatherCode != -1) {
                        val tempMax = Math.round(tempMaxRaw).toInt()
                        val tempMin = Math.round(tempMinRaw).toInt()

                        dailyWeatherList.add(DailyWeather(weatherCode, time, tempMax, tempMin))
                    }
                }

                Log.d("API_RESPONSE_DAILY", "Daily count: ${dailyWeatherList.size}")
                dailyWeatherList

            } catch (e: Exception) {
                Log.e("API_PARSE_ERROR_DAILY", "JSON Parsing failed: ${e.message}")
                e.printStackTrace()
                null
            }
        }

    }
}
