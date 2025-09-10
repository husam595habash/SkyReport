package com.example.apitest.data.model

data class News(
    val title: String,
    val description: String,
    val imageUrl: String,
    val publishedAt: String,
    val url: String,
    val sourceName: String,
    val category: String,
    var isFavorite: Boolean = false
)