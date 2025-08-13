package com.example.apitest.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.apitest.R
import com.example.apitest.data.utils.isUserLoggedIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainSplash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_splash)

        lifecycleScope.launch {
            delay(3000)

            val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val count = prefs.getInt("count", 0)

            if (count == 0) {
                startActivity(Intent(this@MainSplash, StartActivity::class.java))
            } else {
                if (!isUserLoggedIn(this@MainSplash)) {
                    startActivity(Intent(this@MainSplash, Login::class.java))
                } else {
                    startActivity(Intent(this@MainSplash, views::class.java))
                }
            }
            finish()
        }
    }
}
