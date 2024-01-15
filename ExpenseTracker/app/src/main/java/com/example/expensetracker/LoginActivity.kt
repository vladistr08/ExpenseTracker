package com.example.expensetracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var showPasswordCheckBox: CheckBox
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        if(isSessionValid()){
            navigateToMainActivity()
        }

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        setupPasswordVisibilityToggle()
        setupButtonListeners()
    }

    private fun setupPasswordVisibilityToggle() {
        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordEditText.setSelection(passwordEditText.text.length)
        }
    }

    private fun setupButtonListeners() {
        loginButton.setOnClickListener { performLogin() }
        registerButton.setOnClickListener { performRegistration() }
    }

    private fun performLogin() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        saveLoginTimestamp()
                        navigateToMainActivity()
                    } else {
                        Toast.makeText(baseContext, "Login failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun performRegistration() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        saveLoginTimestamp()
                        navigateToMainActivity()
                    } else {
                        Toast.makeText(baseContext, "Registration failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveLoginTimestamp() {
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putLong("lastLoginTimestamp", System.currentTimeMillis())
            apply()
        }
    }

    private fun isSessionValid(): Boolean {
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val lastLoginTimestamp = sharedPref.getLong("lastLoginTimestamp", 0)
        val currentTime = System.currentTimeMillis()

        val sessionDuration = 60 * 60 * 1000

        return (currentTime - lastLoginTimestamp) <= sessionDuration
    }

}
