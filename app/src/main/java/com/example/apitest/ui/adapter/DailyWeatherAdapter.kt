package com.example.apitest.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apitest.R
import com.example.apitest.data.model.DailyWeather
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class DailyWeatherAdapter (private var daily:ArrayList<DailyWeather>) :
    RecyclerView.Adapter<DailyWeatherAdapter.ItemViewHolder>(){
    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_card, parent, false)
        return ItemViewHolder(view)
    }
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val day = itemView.findViewById<TextView>(R.id.dayDaily)
        val minTemp = itemView.findViewById<TextView>(R.id.minTemp)
        val maxTemp = itemView.findViewById<TextView>(R.id.maxTemp)
        val img = itemView.findViewById<ImageView>(R.id.weatherIcon)
    }



    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val day = daily[position]

        holder.minTemp.text = "${day.minTemp}°"
        holder.maxTemp.text = "${day.maxTemp}°"
        when(day.weatherIconResId){
            1000 -> holder.img.setImageResource(R.drawable.sun)
            1100 -> holder.img.setImageResource(R.drawable.mostly_sun)
            1101 -> holder.img.setImageResource(R.drawable.partly_cloudy)
            1102 -> holder.img.setImageResource(R.drawable.cloudy)
            1001 -> holder.img.setImageResource(R.drawable.cloudy)
        }

        holder.day.text = getDayOfWeek(day.dayOfWeek)
    }



    override fun getItemCount(): Int = daily.size

    fun getDayOfWeek(timeString: String): String {
        val zonedDateTime = ZonedDateTime.parse(timeString)
            .withZoneSameInstant(ZoneId.systemDefault())
        return zonedDateTime.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))
    }
}