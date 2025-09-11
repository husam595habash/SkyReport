package com.example.apitest.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.apitest.R
import com.example.apitest.data.repository.Bookmark
import com.example.apitest.data.utils.loadBookmarks
import com.example.apitest.data.utils.loadUsers
import com.example.apitest.data.utils.saveLoggedInUser

class Login : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignIn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signupButton: Button = findViewById(R.id.signupButton)
        signupButton.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }



        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnSignIn = findViewById(R.id.btnLogin)

        btnSignIn.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val users = loadUsers(this)
            val user = users.find { it.username == username && it.password == password }

            if (user != null) {
                saveLoggedInUser(this, user)
                Bookmark.bookmarkList = loadBookmarks(this, user.username)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}