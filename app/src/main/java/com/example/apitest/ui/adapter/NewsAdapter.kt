package com.example.apitest.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apitest.R
import com.example.apitest.data.model.News
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class NewsAdapter(private var newsList: List<News>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.newsTitle)
        val description: TextView = itemView.findViewById(R.id.newsDescription)
        val image: ImageView = itemView.findViewById(R.id.newsImage)
        val category: TextView = itemView.findViewById(R.id.newsCategory)
        val date: TextView = itemView.findViewById(R.id.newsDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }


    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.title.text = news.title
        holder.description.text = news.description
        holder.category.text = news.category
        holder.date.text = formatDate(news.publishedAt)

        if (news.imageUrl.isNotEmpty())
            Picasso.get().load(news.imageUrl).into(holder.image)
        else
            Picasso.get().load("https://thumb.ac-illust.com/b1/b170870007dfa419295d949814474ab2_t.jpeg").into(holder.image)
    }


    private fun formatDate(publishedAt: String): String {
        return try {
            val inFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
            inFmt.timeZone = TimeZone.getTimeZone("UTC")
            val date = inFmt.parse(publishedAt)
            val outFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            outFmt.format(date!!)
        } catch (e: Exception) { publishedAt.take(10) }
    }

    override fun getItemCount() = newsList.size

    fun updateNews(newList: List<News>) {
        newsList = newList
        notifyDataSetChanged()
    }
}
