// com/example/apitest/ui/viewmodel/NewsViewModel.kt
package com.example.apitest.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apitest.data.model.News
import com.example.apitest.data.repository.NewsRepository
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

enum class SortOrder { LATEST, OLDEST }
data class FilterState(
    val categories: Set<String> = emptySet(),
    val query: String = "",
    val sort: SortOrder = SortOrder.LATEST
)

class NewsViewModel : ViewModel() {
    private val _allNews = MutableLiveData<List<News>>(emptyList())

    private val _newsList = MutableLiveData<List<News>>(emptyList())
    val newsList: LiveData<List<News>> = _newsList

    private var filter = FilterState()

    fun fetchAllNews() {
        NewsRepository.getNews { news ->
            val fetched = news ?: emptyList()
            _allNews.postValue(fetched)
            applyFilters()
        }
    }

    fun setSort(order: SortOrder) {
        filter = filter.copy(sort = order)
    }
    fun setCategoryChecked(category: String, checked: Boolean) {
        val s = filter.categories.toMutableSet().apply { if (checked) add(category) else remove(category) }
        filter = filter.copy(categories = s)
    }
    fun clearFilters() { filter = FilterState() }

    fun applyFilters() {
        val all = _allNews.value.orEmpty()

        val byCategory = if (filter.categories.isEmpty()) all
        else all.filter { it.category in filter.categories }

        val q = filter.query.trim()
        val byQuery = if (q.isBlank()) byCategory else byCategory.filter {
            it.title.contains(q, ignoreCase = true)
        }

        val sorted = byQuery.sortedWith { a, b ->
            val da = parseDate(a.publishedAt); val db = parseDate(b.publishedAt)
            when (filter.sort) { SortOrder.LATEST -> db.compareTo(da); SortOrder.OLDEST -> da.compareTo(db) }
        }

        _newsList.postValue(sorted)
    }

    fun setQuery(q: String) { filter = filter.copy(query = q) }


    private fun parseDate(s: String?): ZonedDateTime =
        try { ZonedDateTime.parse(s, DateTimeFormatter.ISO_ZONED_DATE_TIME) }
        catch (_: Exception) { ZonedDateTime.parse("1970-01-01T00:00:00Z") }
}
