package com.example.apitest.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apitest.R
import com.example.apitest.ui.adapter.DailyWeatherAdapter
import com.example.apitest.ui.viewmodel.WeatherViewModel
import com.example.testone.News.hourlyWeatherAdapter

class WeatherFragment : Fragment() {

    private val viewModel: WeatherViewModel by activityViewModels()

    private lateinit var hourlyAdapter: hourlyWeatherAdapter
    private lateinit var hourlyRV: RecyclerView

    private lateinit var dailyAdapter: DailyWeatherAdapter
    private lateinit var dailyRV: RecyclerView

    private lateinit var tempText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.weather, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Views
        hourlyRV = view.findViewById(R.id.hourlyRV)
        dailyRV  = view.findViewById(R.id.dailyRV)
        tempText = view.findViewById(R.id.tempText)

        // Adapters (create once)
        hourlyAdapter = hourlyWeatherAdapter(arrayListOf())
        dailyAdapter  = DailyWeatherAdapter(arrayListOf())

        hourlyRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        hourlyRV.adapter = hourlyAdapter

        dailyRV.layoutManager = LinearLayoutManager(requireContext())
        dailyRV.adapter = dailyAdapter

        // Observers (don’t recreate adapters inside observers)
        viewModel.weatherList.observe(viewLifecycleOwner) { list ->
            list?.let {
                hourlyAdapter.update(it) // add update(...) in your adapter or use submitList if ListAdapter
                if (it.isNotEmpty()) tempText.text = "${it[0].temperature}°"
            }
        }

        viewModel.DailyWeatherList.observe(viewLifecycleOwner) { list ->
            list?.let { dailyAdapter.update(it) }
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            msg?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }

        // Trigger loads (idempotent; ViewModel can cache)
        viewModel.fetchWeather()
        viewModel.fetchDailyWeather()
    }
}
