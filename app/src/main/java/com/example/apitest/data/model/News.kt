package com.example.apitest.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class News(
    val title: String,
    val description: String,
    val imageUrl: String,
    val publishedAt: String,
    val url: String,
    val sourceName: String,
    val category: String,
    val snippet: String,
    var isFavorite: Boolean = false
) : Parcelable