package com.example.betterenglish_mpipproject.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.betterenglish_mpipproject.R
import com.example.betterenglish_mpipproject.ui.login.LoginActivity
import com.example.betterenglish_mpipproject.util.FirebaseAuthProvider
import com.example.betterenglish_mpipproject.util.SharedPreferencesService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var sharedPreferencesService: SharedPreferencesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferencesService = SharedPreferencesService(applicationContext)

        mAuth = FirebaseAuthProvider.getFirebaseAuth()

        val name: EditText = findViewById(R.id.nameRegisterInput)
        val email: EditText = findViewById(R.id.emailRegisterInput)
        val password: EditText = findViewById(R.id.passwordRegisterInput)
        val confirmPassword: EditText = findViewById(R.id.confirmPasswordRegisterInput)

        val registerButton: Button = findViewById(R.id.signupButton)
        val loginButton: TextView = findViewById(R.id.loginLink)


        registerButton.setOnClickListener {
            val nameValue: String = name.text.toString()
            val passwordValue: String = password.text.toString()
            val confirmPasswordValue: String = confirmPassword.text.toString()
            val emailValue: String = email.text.toString()
            validateData(nameValue, emailValue, passwordValue, confirmPasswordValue)
        }

        name.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                name.background = ContextCompat.getDrawable(this, R.drawable.highlighted_border)
            } else {
                name.background = ContextCompat.getDrawable(this, R.drawable.border)
            }

        }
        email.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                email.background = ContextCompat.getDrawable(this, R.drawable.highlighted_border)
            } else {
                email.background = ContextCompat.getDrawable(this, R.drawable.border)
            }

        }
        password.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                password.background = ContextCompat.getDrawable(this, R.drawable.highlighted_border)
            } else {
                password.background = ContextCompat.getDrawable(this, R.drawable.border)
            }

        }
        confirmPassword.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                confirmPassword.background =
                    ContextCompat.getDrawable(this, R.drawable.highlighted_border)
            } else {
                confirmPassword.background = ContextCompat.getDrawable(this, R.drawable.border)
            }

        }

        loginButton.setOnClickListener {
            navigateToLoginActivity()
        }

    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    private fun validateData(
        nameValue: String,
        emailValue: String,
        passwordValue: String,
        confirmPasswordValue: String
    ) {
        if (emailValue.isEmpty() || passwordValue.isEmpty() || nameValue.isEmpty() || confirmPasswordValue.isEmpty()) {
            Toast.makeText(this, "Empty fields!", Toast.LENGTH_LONG).show()
        } else if (!passwordValue.equals(confirmPasswordValue, false)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_LONG).show()
        } else {
            doRegister(nameValue, emailValue, passwordValue)
        }
    }

    private fun doRegister(nameValue: String, usernameValue: String, passwordValue: String) {

        mAuth!!.createUserWithEmailAndPassword(usernameValue, passwordValue)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val currentUser: FirebaseUser? = mAuth!!.currentUser
                    sharedPreferencesService.saveData("uuid", currentUser?.uid.toString())
                    sharedPreferencesService.saveData("name", nameValue)
                    sharedPreferencesService.saveData("email", currentUser?.email.toString())
                    mAuth!!.currentUser?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(nameValue).build())

                    Toast.makeText(this, "Registration successful.", Toast.LENGTH_SHORT).show()
                    navigateToLoginActivity()
                } else {
                    Toast.makeText(this, "Registration failed.", Toast.LENGTH_SHORT).show()
                }

            }
    }
}