package com.example.apitest.ui.activity

import OnboardingAdapter
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
                "Get ready to explore a seamless and intuitive experience designed to keep you informed and organized throughout your day.",
                R.drawable.logo
            ),
            OnboardingPage(
                "Stay Updated with News",
                "Access the latest news from around the world, tailored to your interests so you never miss out on what matters most.",
                R.drawable.img1
            ),
            OnboardingPage(
                "Check Weather Anytime",
                "Keep track of the weather forecast in your location and plan your day confidently with up-to-date weather information.",
                R.drawable.img2
            )
        )

        loginButton.setOnClickListener { startActivity(Intent(this, Login::class.java)) }
        signupButton.setOnClickListener { startActivity(Intent(this, Signup::class.java)) }

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


