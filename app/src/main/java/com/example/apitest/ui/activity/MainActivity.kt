package com.example.apitest.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.airbnb.lottie.LottieAnimationView
import com.example.apitest.R

class MainActivity : AppCompatActivity() {
    lateinit var lottieLogo: LottieAnimationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lottieLogo = findViewById(R.id.splashscreen_icon_view)
    }
}