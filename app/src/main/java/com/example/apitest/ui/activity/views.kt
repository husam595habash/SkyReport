package com.example.apitest.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.apitest.R
import com.example.apitest.data.utils.logoutUser

class views : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_views)

        val logoutBtn: Button = findViewById(R.id.logoutButton)

        logoutBtn.setOnClickListener {
            logoutUser(this)
        }
    }
}