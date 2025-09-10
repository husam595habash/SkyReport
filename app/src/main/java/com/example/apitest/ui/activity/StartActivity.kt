package com.example.apitest.ui.activity

import OnboardingAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.apitest.R
import com.example.apitest.data.model.OnboardingPage
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator


class StartActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var buttonLayout: LinearLayout
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        viewPager = findViewById(R.id.viewPagerOnboarding)
        dotsIndicator = findViewById(R.id.dotsIndicator)
        buttonLayout = findViewById(R.id.buttonLayout)
        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.signupButton)

        val onboardingPages = listOf(
            OnboardingPage(
                "Welcome to Our App!",
                "Your ultimate companion for staying informed with the latest news and weather updates",
                R.drawable.starticon
            ),
            OnboardingPage(
                "Stay Updated with Latest News",
                "Get breaking news, trending stories, and personalized content from trusted sources around the word, all in one place.",
                R.drawable.news_view_pager
            ),
            OnboardingPage(
                "Stay Updated with Real-Time Weather",
                "Get accurate weather forecasts, alerts, and conditions for your location and anywhere in the world",
                R.drawable.weather_view_pager
            )
        )

        loginButton.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().putInt("count", 1).apply()

        }
        signupButton.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
            val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().putInt("count", 1).apply()
        }

        val adapter = OnboardingAdapter(onboardingPages)
        viewPager.adapter = adapter
        dotsIndicator.setViewPager2(viewPager)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val isLastPage = position == onboardingPages.lastIndex
                dotsIndicator.visibility = if (isLastPage) View.GONE else View.VISIBLE
                buttonLayout.visibility = if (isLastPage) View.VISIBLE else View.GONE
            }
        })
    }
}


