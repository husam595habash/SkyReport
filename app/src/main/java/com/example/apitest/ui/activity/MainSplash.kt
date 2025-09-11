package com.example.apitest.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.apitest.R
import com.example.apitest.data.model.News
import com.example.apitest.data.repository.NewsRepository
import com.example.apitest.data.utils.isUserLoggedIn
import com.example.apitest.ui.viewmodel.NewsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// MainSplash.kt
class MainSplash : AppCompatActivity() {

    private lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_splash)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val count = prefs.getInt("count", 0)

        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]

        if (count == 0) {
            // First time user → show logo splash for 3s
            lifecycleScope.launch {
                delay(3000)
                startActivity(Intent(this@MainSplash, StartActivity::class.java))
                finish()
            }
        } else if (!isUserLoggedIn(this)) {
            startActivity(Intent(this, Login::class.java))
            finish()
        } else {
            // User is logged in → fetch news THEN open main
            viewModel.fetchNews {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}

