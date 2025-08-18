package com.example.apitest.data.model

data class Task(
    val id: Long = System.currentTimeMillis(),
    var title: String? = "",
    var date: String? = "",
    var isChecked: Boolean = false
)

