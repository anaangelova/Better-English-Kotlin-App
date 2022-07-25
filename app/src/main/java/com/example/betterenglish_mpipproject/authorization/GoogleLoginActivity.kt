package com.example.betterenglish_mpipproject.authorization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.betterenglish_mpipproject.HomeActivity
import com.example.betterenglish_mpipproject.LoginActivity
import com.example.betterenglish_mpipproject.SharedPreferencesService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleLoginActivity: Activity() {

    private   var auth: FirebaseAuth? = null
    private lateinit var sharedPreferencesService: SharedPreferencesService
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInOptions: GoogleSignInOptions
    val Req_Code:Int=123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuthProvider.getFirebaseAuth();
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("865488890271-jn2q8rcd4nsbh3g85mq0slri0589aqep.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(applicationContext, googleSignInOptions)
        sharedPreferencesService = SharedPreferencesService(applicationContext)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }

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

    private fun checkIfLoggedIn(): Boolean {
        return auth?.currentUser != null;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if(requestCode==Req_Code){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)

        }
    }


    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try {
            val account: GoogleSignInAccount? =completedTask.getResult(ApiException::class.java)
            if (account != null) {
                sharedPreferencesService.saveData("uuid", "")
                sharedPreferencesService.saveData("name", account.displayName!!)
                sharedPreferencesService.saveData("email", account.email!!)
                UpdateUI(account)
            }
        } catch (e: ApiException){
            if(e.statusCode == 12501) {
                val intent = Intent(this, LoginActivity::class.java);
                startActivity(intent);
                finish();
            }
        }
    }

    private fun UpdateUI(account: GoogleSignInAccount){
        val credential= GoogleAuthProvider.getCredential(account.idToken,null)
        auth?.signInWithCredential(credential)?.addOnCompleteListener {task->
            if(task.isSuccessful) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }


}