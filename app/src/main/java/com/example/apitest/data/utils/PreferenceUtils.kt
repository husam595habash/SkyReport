package com.example.apitest.data.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit
import com.example.apitest.data.model.News
import com.example.apitest.data.model.Task
import com.example.apitest.data.model.User
import com.example.apitest.data.repository.Bookmark
import com.example.apitest.ui.activity.Login

fun saveUsers(context: Context, users: List<User>) {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = gson.toJson(users)
    prefs.edit().putString("users_list", json).apply()
}

fun loadUsers(context: Context): MutableList<User> {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = prefs.getString("users_list", null) ?: return mutableListOf()
    val type = object : TypeToken<MutableList<User>>() {}.type
    return gson.fromJson(json, type)
}

fun saveLoggedInUser(context: Context, user: User) {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = gson.toJson(user)
    prefs.edit().putString("logged_in_user", json).putBoolean("is_logged_in", true).apply()
}

fun getLoggedInUser(context: Context): User? {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = prefs.getString("logged_in_user", null) ?: return null
    return gson.fromJson(json, User::class.java)
}

fun logoutUser(context: Context) {
    val prefs = context. getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
    prefs.edit { putBoolean("is_logged_in", false).remove("logged_in_user") }

    Bookmark.bookmarkList.clear()

    val intent = Intent(context, Login::class.java)
    context.startActivity(intent)
    if (context is AppCompatActivity) {
        context.finish()
    }}


fun isUserLoggedIn(context: Context): Boolean {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return prefs.getBoolean("is_logged_in", false)
}

fun saveUserDarkMode(context: Context, username: String, isDarkMode: Boolean) {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    prefs.edit { putBoolean("dark_mode_$username", isDarkMode) }
}

fun loadUserDarkMode(context: Context, username: String): Boolean {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return prefs.getBoolean("dark_mode_$username", false)
}

fun saveToDoList(context: Context, username: String, toDoList: ArrayList<Task>) {
    val prefs = context.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
    prefs.edit{
        val gson = Gson()
        val json = gson.toJson(toDoList)

        putString("to_do_list_$username", json)
    }
}

fun loadToDoList(context: Context, username: String): ArrayList<Task> {
    val prefs = context.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
    val gson = Gson()
    val json = prefs.getString("to_do_list_$username", null)

    return if (json != null) {
        val type = object : TypeToken<ArrayList<Task>>() {}.type
        gson.fromJson(json, type)
    } else {
        ArrayList()
    }
}


fun saveBookmarks(context: Context, username: String, bookmarkList: ArrayList<News>) {
    val prefs = context.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
    prefs.edit {

        val gson = Gson()
        val json = gson.toJson(bookmarkList)

        putString("bookmark_list_$username", json)
    }
}

fun loadBookmarks(context: Context, username:String): ArrayList<News> {
    val prefs = context.getSharedPreferences("settings", Application.MODE_PRIVATE)
    val gson = Gson()
    val json = prefs.getString("bookmark_list_$username", null)

    if (json != null) {
        val type = object : TypeToken<ArrayList<News>>() {}.type
        return gson.fromJson(json, type)
    } else {
        return ArrayList()
    }
}
