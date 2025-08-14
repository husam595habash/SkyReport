package com.example.apitest.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.example.apitest.R
import com.example.apitest.data.model.User
import com.example.apitest.data.utils.getLoggedInUser
import com.example.apitest.data.utils.loadUserDarkMode
import com.example.apitest.data.utils.loadUsers
import com.example.apitest.data.utils.logoutUser
import com.example.apitest.data.utils.saveLoggedInUser
import com.example.apitest.data.utils.saveUserDarkMode
import com.example.apitest.data.utils.saveUsers
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
    private lateinit var backButton: ImageButton

    private lateinit var usernameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var saveButton: Button
    private lateinit var usernameLogo: ImageView
    private lateinit var emailLogo: ImageView
    private lateinit var passwordLogo: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        usernameLogo = findViewById(R.id.usernameLogo)
        emailLogo = findViewById(R.id.emailLogo)
        passwordLogo = findViewById(R.id.passwordLogo)

        username = findViewById(R.id.username)
        email = findViewById(R.id.email)
        imgText = findViewById(R.id.imageText)

        mySwitch = findViewById(R.id.my_switch)
        darkmodeIMG = findViewById(R.id.darkmodeImage)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        usernameET = findViewById(R.id.usernameET)
        emailET = findViewById(R.id.emailET)
        passwordET = findViewById(R.id.passwordET)

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
            darkMode(isDark)
        }


        logoutButton = findViewById(R.id.logoutLayout)
        logoutButton.setOnClickListener{
            logoutUser(this)
            darkMode(false)
        }

        viewModel.showEditProfile.observe(this) { show ->
            toggle(show)
        }

        findViewById<View>(R.id.editProfileCard).setOnClickListener {
            viewModel.toggleEditProfileVisibility()
        }

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            viewModel.toggleEditProfileVisibility()
        }

        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            val username = usernameET.text.toString()
            val email = emailET.text.toString()
            val password = passwordET.text.toString()

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val users = loadUsers(this)

            if (users.any { it.username == username && it.username != getLoggedInUser(this)?.username }) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loggedInUser = getLoggedInUser(this)
            if (loggedInUser != null) {
                val index = users.indexOfFirst { it.username == loggedInUser.username }
                if (index != -1) {
                    users[index] = User(username, email, password)
                    saveUsers(this, users)
                    saveLoggedInUser(this, users[index])
                    updateDetails(users[index])
                    viewModel.toggleEditProfileVisibility()
                    passwordET.text.clear()
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
            }
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

        usernameET.setText(user.username)
        emailET.setText(user.email)
    }

    private fun darkMode(isDark: Boolean) {
        val loggedInUser = getLoggedInUser(this)
        if (loggedInUser != null) {
            saveUserDarkMode(this, loggedInUser.username, isDark)
        }

        if (isDark){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            darkmodeIMG.setImageResource(R.drawable.day)
            usernameLogo.setImageResource(R.drawable.white_usernmae)
            emailLogo.setImageResource(R.drawable.white_email)
            passwordLogo.setImageResource(R.drawable.white_password)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            darkmodeIMG.setImageResource(R.drawable.night)
            usernameLogo.setImageResource(R.drawable.username)
            emailLogo.setImageResource(R.drawable.mail_24dp_000000_fill1_wght400_grad0_opsz24)
            passwordLogo.setImageResource(R.drawable.password)
        }
    }

    private fun toggle(show: Boolean){
        val includeLayout = findViewById<View>(R.id.editProfile)
        val parent = findViewById<RelativeLayout>(R.id.parent)
        val transition: Transition = Slide(Gravity.END)
        transition.duration = 200
        transition.addTarget(includeLayout)
        TransitionManager.beginDelayedTransition(parent, transition)
        includeLayout?.visibility = if (show) View.VISIBLE else View.GONE
    }
}