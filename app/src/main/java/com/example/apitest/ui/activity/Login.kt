package com.example.apitest.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.apitest.R

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signupButton: Button = findViewById(R.id.signupButton)
        signupButton.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }
    }
}