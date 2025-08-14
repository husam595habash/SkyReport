package com.example.apitest.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.example.apitest.R
import com.example.apitest.data.model.User
import com.example.apitest.data.utils.getLoggedInUser
import com.example.apitest.data.utils.loadUserDarkMode
import com.example.apitest.data.utils.logoutUser
import com.example.apitest.data.utils.saveUserDarkMode
import com.example.apitest.ui.viewmodel.SettingsViewModel

class settings : AppCompatActivity() {
    private lateinit var mySwitch: SwitchCompat
    private lateinit var todoListButton: LinearLayout
    private lateinit var logoutButton: LinearLayout
    private lateinit var username: TextView
    private lateinit var email: TextView
    private lateinit var imgText: TextView
    private lateinit var darkmodeIMG: ImageView
    private lateinit var viewModel: SettingsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        username = findViewById(R.id.username)
        email = findViewById(R.id.email)
        imgText = findViewById(R.id.imageText)

        mySwitch = findViewById(R.id.my_switch)
        darkmodeIMG = findViewById(R.id.darkmodeImage)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        var user = getLoggedInUser(this)
        if (user != null) {
            updateDetails(user)
            viewModel.initDarkMode(this, user.username)
            mySwitch.isChecked = viewModel.isDarkMode.value ?: false
        }


        mySwitch.setOnCheckedChangeListener{_, isChecked ->
            if (user != null)
                viewModel.toggleDarkMode(isChecked, this, user.username)
        }


        viewModel.isDarkMode.observe(this) {isDark ->
            AppCompatDelegate.setDefaultNightMode(
                if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            darkmodeIMG.setImageResource(if (isDark) R.drawable.day else R.drawable.night)
        }
        logoutButton = findViewById(R.id.logoutLayout)
        logoutButton.setOnClickListener{
            logoutUser(this)
        }


    }

    private fun updateDetails(user: User){
        val username = user.username
        val firstInitial = username[0]

        val spaceIndex = username.indexOf(" ")
        val secondInitial = if (spaceIndex != -1 && spaceIndex + 1 < username.length) {
            username[spaceIndex + 1]
        } else {
            null
        }

        val text = if (secondInitial != null) "$firstInitial$secondInitial" else "$firstInitial"

        val initials = text.uppercase()

        imgText.text = initials

        this.username.text = username
        email.text = user.email
    }
}