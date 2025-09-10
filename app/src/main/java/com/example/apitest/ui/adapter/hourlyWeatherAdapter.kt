package com.example.testone.News

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apitest.R
import com.example.apitest.data.model.hourlyWeather
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class hourlyWeatherAdapter (private var hourly:ArrayList<hourlyWeather>) :
    RecyclerView.Adapter<hourlyWeatherAdapter.ItemViewHolder>(){
    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hourly_card, parent, false)
        return ItemViewHolder(view)
    }
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time = itemView.findViewById<TextView>(R.id.hourlyTime)
        val temp = itemView.findViewById<TextView>(R.id.hourlyTemp)
        val img = itemView.findViewById<ImageView>(R.id.hourlyImage)
    }



    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val hour = hourly[position]
        if (position==0)
            holder.time.text = "Now"
        else
            holder.time.text = convertToHourOnly(hour.time)
        holder.temp.text = "${hour.temperature}°"

        when(hour.weatherCode){
            1000 -> holder.img.setImageResource(R.drawable.sun)
            1100 -> holder.img.setImageResource(R.drawable.mostly_sun)
            1101 -> holder.img.setImageResource(R.drawable.partly_cloudy)
            1102 -> holder.img.setImageResource(R.drawable.cloudy)
            1001 -> holder.img.setImageResource(R.drawable.cloudy)
        }
    }

    fun convertToHourOnly(isoTime: String): String {
        val zonedDateTime = ZonedDateTime.parse(isoTime)
            .withZoneSameInstant(ZoneId.systemDefault()) // Convert from UTC to device local time

        val formatter = DateTimeFormatter.ofPattern("h a")
        return formatter.format(zonedDateTime)
    }

    fun update(items: List<hourlyWeather>) {
        hourly.clear()
        hourly.addAll(items)
        notifyDataSetChanged()
    }




    override fun getItemCount(): Int = hourly.size
}