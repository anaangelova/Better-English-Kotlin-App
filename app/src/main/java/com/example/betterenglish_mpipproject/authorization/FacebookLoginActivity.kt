package com.example.betterenglish_mpipproject.authorization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.betterenglish_mpipproject.HomeActivity
import com.example.betterenglish_mpipproject.LoginActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class FacebookLoginActivity : Activity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuthProvider.getFirebaseAuth();
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logInWithReadPermissions(
            this,
            Arrays.asList("email", "public_profile")
        );
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    auth.signInWithCredential(credential).addOnCompleteListener {
                            task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            updateUI()
                        } else {
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                            updateUI()
                        }
                    }
                }
                override fun onCancel() {
                    navigateToLogin()
                }
                override fun onError(exception: FacebookException) {}
            }
        )

    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        if (checkIfLoggedIn()) {
            navigateOut()
        }
    }

    private fun navigateOut() {
        val intent = Intent(this, HomeActivity::class.java);
        startActivity(intent);
        finish();
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java);
        startActivity(intent);
        finish();
    }

    private fun checkIfLoggedIn(): Boolean {
        return auth.currentUser != null;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateUI() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}