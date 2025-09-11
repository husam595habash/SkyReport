package com.example.apitest.ui.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apitest.R
import com.example.apitest.data.model.News
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import androidx.core.net.toUri

class DetailsPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.news_details)

        val news = intent.getParcelableExtra<News>("news")

        val image = findViewById<ImageView>(R.id.newsImage)
        val source = findViewById<TextView>(R.id.sourceName)
        val date = findViewById<TextView>(R.id.publishDate)
        val title = findViewById<TextView>(R.id.title)
        val description = findViewById<TextView>(R.id.description)
        val snippet = findViewById<TextView>(R.id.snippet)

        if (news != null) {
            title.text = news.title
            description.text = news.description
            snippet.text = news.snippet
            source.text = news.sourceName
            date.text = formatDate(news.publishedAt)

            if (news.imageUrl.isNotEmpty())
                Picasso.get().load(news.imageUrl).into(image)
            else
                Picasso.get().load("https://thumb.ac-illust.com/b1/b170870007dfa419295d949814474ab2_t.jpeg").into(image)

            val backButton = findViewById<ImageView>(R.id.backButton)
            backButton.setOnClickListener {
                finish()
            }

            val shareButton = findViewById<ImageView>(R.id.imageButton)
            shareButton.setOnClickListener {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_SUBJECT, news.title)
                    putExtra(Intent.EXTRA_TEXT, news.description ?: news.snippet ?: "")
                }

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send email using:"))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
                }
                }

            val sourceUrl = news.url
            val sourceUrlTextView = findViewById<TextView>(R.id.source)
            sourceUrlTextView.text = sourceUrl
            sourceUrlTextView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sourceUrl))
                startActivity(intent)
            }


        }
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
}