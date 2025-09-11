// com/example/apitest/ui/viewmodel/NewsViewModel.kt
package com.example.apitest.ui.viewmodel

import androidx.lifecycle.*
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

    // Raw data (latest from repository)
    private var allNews: List<News> = emptyList()

    // Applied vs Pending (Apply button will commit pending -> applied)
    private var applied = FilterState()
    private var pending = FilterState()

    // Exposed results (the Fragment observes this)
    private val _filtered = MediatorLiveData<List<News>>()
    val newsList: LiveData<List<News>> = _filtered

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // A simple “recompute now” trigger
    private val tick = MutableLiveData<Unit>()

    init {
        // Recompute when repository pushes new data
        _filtered.addSource(NewsRepository.newsList) { list ->
            allNews = list ?: emptyList()
            _filtered.value = compute(allNews, applied)
        }
        // Recompute when we “tick” (Apply pressed or search typed)
        _filtered.addSource(tick) {
            _filtered.value = compute(allNews, applied)
        }
    }

    fun fetchNews(onDone: () -> Unit = {}) {
        _isLoading.postValue(true)
        NewsRepository.fetchNews {
            // data arrival triggers via addSource above
            _isLoading.postValue(false)
            onDone()
        }
    }

    // ------------ user actions -------------
    fun setSort(order: SortOrder) { pending = pending.copy(sort = order) }

    fun setCategoryChecked(category: String, checked: Boolean) {
        val s = pending.categories.toMutableSet().apply {
            if (checked) add(category) else remove(category)
        }
        pending = pending.copy(categories = s)
    }

    fun clearFilters() {
        pending = FilterState()
        applied = applied.copy(categories = emptySet(), sort = SortOrder.LATEST, query = "")
        tick.postValue(Unit) // reflect cleared state immediately (list resets)
    }

    // Search should be live: apply immediately to "applied" AND keep it in pending too
    fun setQuery(q: String) {
        pending = pending.copy(query = q)
        applied = applied.copy(query = q)
        tick.postValue(Unit)
    }

    // Apply button commits pending categories/sort
    fun applyFilters() {
        applied = applied.copy(
            categories = pending.categories,
            sort = pending.sort
            // query stays as already applied live by setQuery()
        )
        tick.postValue(Unit)
    }

    // ------------ helpers -------------
    private fun compute(all: List<News>, f: FilterState): List<News> {
        val byCategory = if (f.categories.isEmpty()) all else all.filter { it.category in f.categories }
        val q = f.query.trim()
        val byQuery = if (q.isBlank()) byCategory else byCategory.filter {
            it.title.contains(q, true) || (it.description?.contains(q, true) == true)
        }
        return byQuery.sortedWith { a, b ->
            val da = parseDate(a.publishedAt); val db = parseDate(b.publishedAt)
            when (f.sort) {
                SortOrder.LATEST -> db.compareTo(da)
                SortOrder.OLDEST -> da.compareTo(db)
            }
        }
    }

    private fun parseDate(s: String?): ZonedDateTime =
        try { ZonedDateTime.parse(s, DateTimeFormatter.ISO_ZONED_DATE_TIME) }
        catch (_: Exception) { ZonedDateTime.parse("1970-01-01T00:00:00Z") }
}
