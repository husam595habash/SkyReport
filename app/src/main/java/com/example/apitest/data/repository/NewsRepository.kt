// data/repository/NewsRepository.kt
package com.example.apitest.data.repository

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.apitest.data.api.NewsApiService
import com.example.apitest.data.model.News

object NewsRepository {
    private val _newsList = MutableLiveData<List<News>>()
    val newsList: LiveData<List<News>> get() = _newsList

    private val main = Handler(Looper.getMainLooper())

    fun fetchNews(onDone: () -> Unit = {}) {
        NewsApiService.fetchNews { fetched ->
            // Just replace the old list with the new one
            val incoming = fetched.orEmpty()
            _newsList.postValue(incoming)

            main.post(onDone)
        }
    }
}
