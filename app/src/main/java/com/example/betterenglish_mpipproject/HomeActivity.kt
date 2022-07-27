package com.example.betterenglish_mpipproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.betterenglish_mpipproject.authorization.FirebaseAuthProvider
import com.example.betterenglish_mpipproject.enums.Difficulty
import com.example.betterenglish_mpipproject.model.Level
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private lateinit var wordsApi: WordsApi
    private lateinit var homeActivityViewModel: HomeActivityViewModel
    private lateinit var sharedPreferencesService: SharedPreferencesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuthProvider.getFirebaseAuth();
        sharedPreferencesService = SharedPreferencesService(applicationContext)
        wordsApi = WordsApiClient.getWordsApi()!!

        homeActivityViewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        homeActivityViewModel.getWordLiveData().observe(this, object : Observer<RandomWord> {
            override fun onChanged(t: RandomWord?) {
                showRandomWord(t)
            }
        })
        populateLevelContainers()
        populateRandomWordContainter()

        val randomWordContainer: RelativeLayout = findViewById(R.id.randomWordContainer)
        randomWordContainer.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                homeActivityViewModel.getRandomWord()
            }
        }

        val greetingsTextView: TextView = findViewById(R.id.greetingTextView)
        greetingsTextView.text = ""
        val username = sharedPreferencesService.getData("name")
        greetingsTextView.text = "Hello, " + username

        FacebookSdk.sdkInitialize(applicationContext);
        AppEventsLogger.activateApp(application);

        val logoutButton: FloatingActionButton = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val providerData = currentUser?.providerData
            if (providerData?.size!! > 1) {
                if (providerData.get(1)?.providerId.equals("google.com")) {
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
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

        val mediumButton: Button = findViewById(R.id.playMediumButton)
        mediumButton.setOnClickListener {
            homeActivityViewModel.getLevelsForUser(mAuth?.currentUser?.uid!!).observe(this, {
                var list = it.levels
                var mediumLevel: Level? =
                    list?.first { el -> el.difficulty.toString() == "INTERMEDIATE" }

                if(!mediumLevel?.isUnlocked!!) {
                    val builder = AlertDialog.Builder(this)
                        .setTitle("Unachievable level")
                        .setMessage("You need to pass the beginner level first!")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setCancelable(false)
                        .setNeutralButton("OK") { _, _ -> }
                   builder.create().show()

                }

            })
        }
        val hardButton: Button = findViewById(R.id.playHardButton)
        hardButton.setOnClickListener {
            homeActivityViewModel.getLevelsForUser(mAuth?.currentUser?.uid!!).observe(this, {
                var list = it.levels
                var hardLevel: Level? =
                    list?.first { el -> el.difficulty.toString() == "ADVANCED" }

                if(!hardLevel?.isUnlocked!!) {
                    val builder = AlertDialog.Builder(this)
                        .setTitle("Unachievable level")
                        .setMessage("You need to pass the intermediate level first!")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setCancelable(false)
                        .setNeutralButton("OK") { _, _ -> }
                    builder.create().show()

                }

            })
        }


    }

    private fun populateLevelContainers() {
        homeActivityViewModel.getLevelsForUser(mAuth?.currentUser?.uid!!).observe(this, {
            var beginnerTextView: TextView = findViewById(R.id.passedBeginnerTextView)
            var intermediateTextView: TextView = findViewById(R.id.passedMediumTextView)
            var advancedTextView: TextView = findViewById(R.id.passedHardTextView)

            var easyLevel: Level
            var mediumLevel: Level
            var hardLevel: Level

            if (it.levels?.isEmpty() == true) {
                easyLevel =
                    Level(Difficulty.BEGINNER, mAuth?.uid.toString(), true, 0)
                mediumLevel =
                    Level(Difficulty.INTERMEDIATE, mAuth?.uid.toString(), false, 0)
                hardLevel =
                    Level(Difficulty.ADVANCED, mAuth?.uid.toString(), false, 0)
                homeActivityViewModel.addLevel(easyLevel)
                homeActivityViewModel.addLevel(mediumLevel)
                homeActivityViewModel.addLevel(hardLevel)

                populateLevelsUI(beginnerTextView,intermediateTextView,advancedTextView, easyLevel, mediumLevel, hardLevel)
            } else {
                easyLevel =
                    it.levels?.first { el -> el.difficulty.toString() == "BEGINNER" }!!
                mediumLevel =
                    it.levels?.first { el -> el.difficulty.toString() == "INTERMEDIATE" }!!
                hardLevel =
                    it.levels?.first { el -> el.difficulty.toString() == "ADVANCED" }!!
                populateLevelsUI(beginnerTextView,intermediateTextView,advancedTextView, easyLevel, mediumLevel, hardLevel)
            }
        })

    }

    private fun populateLevelsUI(beginnerTextView: TextView, intermediateTextView: TextView, advancedTextView: TextView, easyLevel: Level, mediumLevel: Level, hardLevel: Level) {
        beginnerTextView.text = ""
        beginnerTextView.text = easyLevel.passedQuestions.toString() + " / 10 passed questions"
        intermediateTextView.text = ""
        intermediateTextView.text = mediumLevel.passedQuestions.toString() + " / 10 passed questions"
        advancedTextView.text = ""
        advancedTextView.text = hardLevel.passedQuestions.toString() + " / 10 passed questions"
    }

    private fun populateRandomWordContainter() {
        CoroutineScope(Dispatchers.IO).launch {
            homeActivityViewModel.getRandomWord()
        }
    }

    private fun showRandomWord(t: RandomWord?) {
        var randomTextView: TextView = findViewById(R.id.randomWordTextView)
        randomTextView.text = ""
        randomTextView.text = t?.word

        var partOfSpeechTextView: TextView = findViewById(R.id.partOfSpeechTextView)
        partOfSpeechTextView.text = ""
        partOfSpeechTextView.text = (t?.definitions?.get(0)?.partOfSpeech ?: "")

        var definitionTextView: TextView = findViewById(R.id.definitionTextView)
        definitionTextView.text = ""
        definitionTextView.text = "def. " + (t?.definitions?.get(0)?.definition ?: "")

    }

    override fun onStart() {
        super.onStart()
        if (!checkIfLoggedIn()) {
            navigateToLoginActivity()
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java);
        startActivity(intent);
        finish();
    }

    private fun checkIfLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null;
    }
}