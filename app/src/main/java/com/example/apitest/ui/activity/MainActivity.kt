package com.example.apitest.ui.activity

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.apitest.R
import com.example.apitest.data.utils.getLoggedInUser
import com.example.apitest.data.utils.loadUserDarkMode
import com.example.apitest.ui.fragment.NewsFragment
import com.example.apitest.ui.fragment.SettingsFragment
import com.example.apitest.ui.fragment.WeatherFragment
import com.example.apitest.ui.viewmodel.NewsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: NewsViewModel
    private var updatingUI = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val username = getLoggedInUser(this)?.username
        val isDark = loadUserDarkMode(this, username)

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO)

        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)


        val latest = findViewById<MaterialRadioButton>(R.id.latestRadioButton)
        val oldest = findViewById<MaterialRadioButton>(R.id.oldestRadioButton)

        val worldBtn  = findViewById<MaterialButton>(R.id.generalButton)
        val techBtn   = findViewById<MaterialButton>(R.id.techButton)
        val sportsBtn = findViewById<MaterialButton>(R.id.sportsButton)
        val entBtn    = findViewById<MaterialButton>(R.id.entertainmetnButton)
        val healthBtn = findViewById<MaterialButton>(R.id.healthButton)

        val btnApply = findViewById<View>(R.id.btnApply)
        val btnReset = findViewById<View>(R.id.btnReset)
        val btnClearAll = findViewById<View>(R.id.button)
        val backBtn = findViewById<View>(R.id.backButton)

        latest.setOnCheckedChangeListener { _, checked ->
            if (updatingUI) return@setOnCheckedChangeListener
            if (checked) viewModel.setSort(com.example.apitest.ui.viewmodel.SortOrder.LATEST)
        }
        oldest.setOnCheckedChangeListener { _, checked ->
            if (updatingUI) return@setOnCheckedChangeListener
            if (checked) viewModel.setSort(com.example.apitest.ui.viewmodel.SortOrder.OLDEST)
        }

        worldBtn.addOnCheckedChangeListener { _, isChecked ->
            if (updatingUI) return@addOnCheckedChangeListener
            viewModel.setCategoryChecked("general", isChecked)
        }
        techBtn.addOnCheckedChangeListener { _, isChecked ->
            if (updatingUI) return@addOnCheckedChangeListener
            viewModel.setCategoryChecked("tech", isChecked)
        }
        sportsBtn.addOnCheckedChangeListener { _, isChecked ->
            if (updatingUI) return@addOnCheckedChangeListener
            viewModel.setCategoryChecked("sports", isChecked)
        }
        entBtn.addOnCheckedChangeListener { _, isChecked ->
            if (updatingUI) return@addOnCheckedChangeListener
            viewModel.setCategoryChecked("entertainment", isChecked)
        }
        healthBtn.addOnCheckedChangeListener { _, isChecked ->
            if (updatingUI) return@addOnCheckedChangeListener
            viewModel.setCategoryChecked("health", isChecked)
        }

        // APPLY / RESET / CLEAR
        btnApply.setOnClickListener {
            viewModel.applyFilters()
            toggle(false)
        }
        val resetUi: () -> Unit = {
            updatingUI = true
            listOf(worldBtn, techBtn, sportsBtn, entBtn, healthBtn).forEach { it.isChecked = false }
            latest.isChecked = true
            oldest.isChecked = false
            updatingUI = false
        }

        btnReset.setOnClickListener {
            viewModel.clearFilters()
            viewModel.applyFilters()
            resetUi()
        }
        btnClearAll.setOnClickListener {
            viewModel.clearFilters()
            viewModel.applyFilters()
            resetUi()
        }

        // BACK (top-left)
        backBtn.setOnClickListener { toggle(false) }


        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)


        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_news -> {
                    loadFragment(NewsFragment())
                    true
                }
                R.id.navigation_weather -> {
                    loadFragment(WeatherFragment())
                    true
                }
                R.id.navigation_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.navigation_news
        }


    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
    fun toggle(show: Boolean) {
        val overlay = findViewById<View>(R.id.filterInclude)
        val sceneRoot = findViewById<ViewGroup>(R.id.mainParent)

        val transition = android.transition.Slide(Gravity.END).apply {
            duration = 200
            addTarget(overlay)
        }
        android.transition.TransitionManager.beginDelayedTransition(sceneRoot, transition)
        overlay.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun toggleFilterOverlay() {
        val isVisible = findViewById<View>(R.id.filterInclude).visibility == View.VISIBLE
        toggle(!isVisible)
    }

}
