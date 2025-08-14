package com.example.apitest.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apitest.data.utils.loadUserDarkMode
import com.example.apitest.data.utils.saveUserDarkMode

class SettingsViewModel : ViewModel() {
    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    private val _showEditProfile = MutableLiveData<Boolean>(false)
    val showEditProfile: LiveData<Boolean> get() = _showEditProfile

    fun initDarkMode(context: Context, username: String) {
        val saved = loadUserDarkMode(context, username)
        _isDarkMode.value = saved
    }

    fun toggleDarkMode(isDark: Boolean, context: Context, username: String) {
        saveUserDarkMode(context, username, isDark)
        _isDarkMode.value = isDark
    }

    fun toggleEditProfileVisibility() {
        _showEditProfile.value = _showEditProfile.value?.not() ?: false
    }


}
