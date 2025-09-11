package com.example.apitest.data.api

import android.util.Log
import com.example.apitest.data.model.News
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

object NewsApiService {
    private val client = OkHttpClient()
    private const val API_URL =
        "https://api.thenewsapi.com/v1/news/all?api_token=imvAhVx2TQaJaYCr9WBtCSmut7khrFxUmSVLi5p9"

    fun fetchNews(callback: (List<News>?) -> Unit) {
        val request = Request.Builder()
            .url(API_URL)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    println("⚠️ API error: ${response.code}")
                    callback(null)
                    return
                }

                try {
                    response.body?.use { responseBody ->
                        val json = JSONObject(responseBody.string())
                        val newsArray = json.optJSONArray("data") ?: return callback(emptyList())
                        val newsList = mutableListOf<News>()

                        for (i in 0 until newsArray.length()) {
                            val obj = newsArray.optJSONObject(i) ?: continue

                            val categories = obj.optJSONArray("categories")
                            val firstCategory = if (categories != null && categories.length() > 0) {
                                categories.getString(0).lowercase()
                            } else {
                                "All"
                            }


                            newsList.add(
                                News(
                                    title = obj.optString("title", "Untitled"),
                                    description = obj.optString("description", null),
                                    imageUrl = obj.optString("image_url", null),
                                    publishedAt = obj.optString("published_at", ""),
                                    sourceName = obj.optString("source",""),
                                    url = obj.optString("url", null),
                                    category = firstCategory,
                                    snippet = obj.optString("snippet", null),
                                )
                            )
                        }
                        Log.d("NewsApiService", "Fetched ${newsList.size} news items")
                        callback(newsList)
                    } ?: callback(emptyList())
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(null)
                }
            }
        })
    }
}
