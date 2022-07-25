package com.example.betterenglish_mpipproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.betterenglish_mpipproject.authorization.FirebaseAuthProvider
import com.example.betterenglish_mpipproject.model.RandomWord
import com.example.betterenglish_mpipproject.viewmodel.HomeActivityViewModel
import com.example.betterenglish_mpipproject.wordsApi.WordsApi
import com.example.betterenglish_mpipproject.wordsApi.WordsApiClient
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private lateinit var wordsApi: WordsApi
    private lateinit var homeActivityViewModel: HomeActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuthProvider.getFirebaseAuth();

        wordsApi = WordsApiClient.getWordsApi()!!

        homeActivityViewModel =ViewModelProvider(this).get(HomeActivityViewModel::class.java)

        homeActivityViewModel.getWordLiveData().observe(this, object : Observer<RandomWord> {
            override fun onChanged(t: RandomWord?) {
                showRandomWord(t)
            }
        })


        FacebookSdk.sdkInitialize(applicationContext);
        AppEventsLogger.activateApp(application);

        var logoutButton: FloatingActionButton = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            var currentUser = FirebaseAuth.getInstance().currentUser
            var providerData = currentUser?.providerData
            if(providerData?.size!! > 1) {
                if(providerData.get(1)?.providerId.equals("google.com")) {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    val googleSignInClient = GoogleSignIn.getClient(applicationContext, gso)
                    googleSignInClient.revokeAccess()
                    googleSignInClient.signOut()
                } else if (providerData.get(1)?.providerId.equals("facebook.com")) {
                    LoginManager.getInstance().logOut();
                }
            }
            mAuth?.signOut()
            navigateToLoginActivity()
        }

       // var randomWordButton: Button = findViewById(R.id.randomWordButton)

        var randomWordContainer: RelativeLayout = findViewById(R.id.randomWordContainer)

        randomWordContainer.setOnClickListener {
            homeActivityViewModel.getRandomWord()
        }

        /*randomWordButton.setOnClickListener {
            homeActivityViewModel.getRandomWord()
        }*/
    }

    private fun showRandomWord(t: RandomWord?) {
        var randomTextView: TextView = findViewById(R.id.randomWordTextView)
        randomTextView.text= ""
        randomTextView.text = t?.word

        var partOfSpeechTextView: TextView = findViewById(R.id.partOfSpeechTextView)
        partOfSpeechTextView.text = ""
        partOfSpeechTextView.text = (t?.definitions?.get(0)?.partOfSpeech ?:  "")

        var definitionTextView: TextView = findViewById(R.id.definitionTextView)
        definitionTextView.text = ""
        definitionTextView.text = "def. " + (t?.definitions?.get(0)?.definition ?:  "")

    }

    override fun onStart() {
        super.onStart()
        if(!checkIfLoggedIn()) {
            navigateToLoginActivity()
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java);
        startActivity(intent);
        finish();
    }

    private fun checkIfLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null ;
    }
}