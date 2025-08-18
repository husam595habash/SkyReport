package com.example.apitest.data.model

data class Section(
    val title: String,
    val date: String,
    val category: String,
    val tasks: List<Task> = emptyList(),
    var image: String? = null
)
