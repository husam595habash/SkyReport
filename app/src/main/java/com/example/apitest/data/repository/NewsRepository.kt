package com.example.apitest.data.repository

import com.example.apitest.data.api.NewsApiService
import com.example.apitest.data.model.News

object NewsRepository {
    fun getNews(callback: (List<News>?) -> Unit) {
        NewsApiService.fetchNews(callback)
    }
}
