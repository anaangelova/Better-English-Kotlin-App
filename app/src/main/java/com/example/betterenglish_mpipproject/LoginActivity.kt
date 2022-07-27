package com.example.betterenglish_mpipproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.betterenglish_mpipproject.authorization.FacebookLoginActivity
import com.example.betterenglish_mpipproject.authorization.FirebaseAuthProvider
import com.example.betterenglish_mpipproject.authorization.GoogleLoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private   var mAuth: FirebaseAuth? = null
    private lateinit var sharedPreferencesService: SharedPreferencesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferencesService = SharedPreferencesService(applicationContext)
        mAuth = FirebaseAuthProvider.getFirebaseAuth();

        val username: EditText = findViewById(R.id.emailLoginInput)
        val password: EditText = findViewById(R.id.passwordLoginInput)

        val loginButton: Button = findViewById(R.id.loginButton)
        val registerButton: TextView = findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            var usernameValue: String = username.text.toString()
            var passwordValue: String = password.text.toString()

            if (usernameValue.isEmpty() || passwordValue.isEmpty()) {
                Toast.makeText(this, "Empty fields", Toast.LENGTH_LONG).show();
            } else {
                doLogin(usernameValue, passwordValue);
            }
        }

        username.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                username.background = ContextCompat.getDrawable(this, R.drawable.highlighted_border)
            } else {
                username.background = ContextCompat.getDrawable(this, R.drawable.border)
            }

        }

        password.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                password.background = ContextCompat.getDrawable(this, R.drawable.highlighted_border)
            } else {
                password.background = ContextCompat.getDrawable(this, R.drawable.border)
            }
        }

        registerButton.setOnClickListener {
            navigateToRegisterActivity()
        }

        var googleButton: ImageView = findViewById(R.id.googleSignInButton)
        googleButton.setOnClickListener {
            val intent = Intent(this, GoogleLoginActivity::class.java);
            startActivity(intent);
            finish();
        }

        var facebookButton: ImageView = findViewById(R.id.facebookSignInButton)
        facebookButton.setOnClickListener {
            val intent = Intent(this, FacebookLoginActivity::class.java);
            startActivity(intent);
            finish();
        }

    }

    override fun onStart() {
        super.onStart()
        if (checkIfLoggedIn()) {
            navigateOut()
        }
    }

    private fun doLogin(usernameValue: String, passwordValue: String) {
        mAuth!!.signInWithEmailAndPassword(usernameValue, passwordValue)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val currentUser: FirebaseUser? = mAuth?.currentUser
                    sharedPreferencesService.saveData("uuid", currentUser?.uid.toString())
                    sharedPreferencesService.saveData("name", currentUser?.displayName.toString())
                    sharedPreferencesService.saveData("email", currentUser?.email.toString())
                    navigateOut()
                } else {
                    Toast.makeText(this, "Login failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateOut() {
        val intent = Intent(this, HomeActivity::class.java);
        startActivity(intent);
        finish();
    }

    private fun navigateToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java);
        startActivity(intent);
        finish()
    }

    private fun checkIfLoggedIn(): Boolean {
        return mAuth?.currentUser != null;
    }

}