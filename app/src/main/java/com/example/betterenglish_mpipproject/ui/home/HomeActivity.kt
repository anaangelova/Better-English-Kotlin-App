package com.example.betterenglish_mpipproject.ui.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.betterenglish_mpipproject.R
import com.example.betterenglish_mpipproject.util.FirebaseAuthProvider
import com.example.betterenglish_mpipproject.util.Difficulty
import com.example.betterenglish_mpipproject.domain.level.model.Level
import com.example.betterenglish_mpipproject.domain.word.RandomWord
import com.example.betterenglish_mpipproject.data.WordsApi
import com.example.betterenglish_mpipproject.data.WordsApiClient
import com.example.betterenglish_mpipproject.domain.hub.KnowledgeHubActivity
import com.example.betterenglish_mpipproject.ui.login.LoginActivity
import com.example.betterenglish_mpipproject.ui.quiz.QuizActivity
import com.example.betterenglish_mpipproject.util.SharedPreferencesService
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

        mAuth = FirebaseAuthProvider.getFirebaseAuth()
        sharedPreferencesService = SharedPreferencesService(applicationContext)
        wordsApi = WordsApiClient.getWordsApi()!!

        homeActivityViewModel = ViewModelProvider(this).get(HomeActivityViewModel::class.java)
        homeActivityViewModel.getWordLiveData().observe(this, object : Observer<RandomWord> {
            override fun onChanged(t: RandomWord?) {
                showRandomWord(t)
            }
        })
        populateLevelContainers()
        populateRandomWordContainer()

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

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)

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
                    LoginManager.getInstance().logOut()
                }
            }
            mAuth?.signOut()
            navigateToLoginActivity()
        }

        val easyButton: Button = findViewById(R.id.playBeginnerButton)
        easyButton.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("userId", mAuth?.currentUser?.uid)
            intent.putExtra("level", 0)
            startActivity(intent)
            finish()
        }

        val mediumButton: Button = findViewById(R.id.playMediumButton)
        mediumButton.setOnClickListener {
            homeActivityViewModel.getLevelsForUser(mAuth?.currentUser?.uid!!).observe(this, {
                val list = it.levels
                val mediumLevel: Level? =
                    list?.last { el -> el.difficulty.toString() == "INTERMEDIATE" }

                if (!mediumLevel?.isUnlocked!!) {
                    val builder = AlertDialog.Builder(this)
                    val view =
                        LayoutInflater.from(this).inflate(R.layout.locked_level_alert_dialog, null)
                    builder.setView(view)

                    val infoMessage: TextView = view.findViewById(R.id.infoMessageTextView)
                    val closeButton: ImageView = view.findViewById(R.id.closeButton)
                    infoMessage.text = "You need to pass the beginner level first!"
                    val dialog = builder.create()
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    closeButton.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                } else {
                    val intent = Intent(this, QuizActivity::class.java)
                    intent.putExtra("userId", mAuth?.currentUser?.uid)
                    intent.putExtra("level", 1)
                    startActivity(intent)
                    finish()
                }

            })
        }
        val hardButton: Button = findViewById(R.id.playHardButton)
        hardButton.setOnClickListener {
            homeActivityViewModel.getLevelsForUser(mAuth?.currentUser?.uid!!).observe(this, {
                val list = it.levels
                val hardLevel: Level? =
                    list?.last { el -> el.difficulty.toString() == "ADVANCED" }

                if (!hardLevel?.isUnlocked!!) {
                    val builder = AlertDialog.Builder(this)
                    val view =
                        LayoutInflater.from(this).inflate(R.layout.locked_level_alert_dialog, null)
                    builder.setView(view)

                    val infoMessage: TextView = view.findViewById(R.id.infoMessageTextView)
                    val closeButton: ImageView = view.findViewById(R.id.closeButton)
                    infoMessage.text = "You need to pass the intermediate level first!"
                    val dialog = builder.create()
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    closeButton.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                } else {
                    val intent = Intent(this, QuizActivity::class.java)
                    intent.putExtra("userId", mAuth?.currentUser?.uid)
                    intent.putExtra("level", 2)
                    startActivity(intent)
                    finish()
                }

            })
        }

        val openAdditionalResourcesButton: Button = findViewById(R.id.openAdditionalResourcesButton)
        openAdditionalResourcesButton.setOnClickListener {
            val intent = Intent(this, KnowledgeHubActivity::class.java)
            startActivity(intent)
        }

    }

    private fun populateLevelContainers() {
        homeActivityViewModel.getLevelsForUser(mAuth?.currentUser?.uid!!).observe(this, {
            val beginnerTextView: TextView = findViewById(R.id.passedBeginnerTextView)
            val intermediateTextView: TextView = findViewById(R.id.passedMediumTextView)
            val advancedTextView: TextView = findViewById(R.id.passedHardTextView)

            val easyLevel: Level
            val mediumLevel: Level
            val hardLevel: Level

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

                populateLevelsUI(
                    beginnerTextView,
                    intermediateTextView,
                    advancedTextView,
                    easyLevel,
                    mediumLevel,
                    hardLevel
                )
            } else {
                easyLevel =
                    it.levels?.last { el -> el.difficulty.toString() == "BEGINNER" }!!
                mediumLevel =
                    it.levels?.last { el -> el.difficulty.toString() == "INTERMEDIATE" }!!
                hardLevel =
                    it.levels?.last { el -> el.difficulty.toString() == "ADVANCED" }!!
                populateLevelsUI(
                    beginnerTextView,
                    intermediateTextView,
                    advancedTextView,
                    easyLevel,
                    mediumLevel,
                    hardLevel
                )
            }
        })

    }

    private fun populateLevelsUI(
        beginnerTextView: TextView,
        intermediateTextView: TextView,
        advancedTextView: TextView,
        easyLevel: Level,
        mediumLevel: Level,
        hardLevel: Level
    ) {
        beginnerTextView.text = ""
        beginnerTextView.text = easyLevel.passedQuestions.toString() + " / 13 passed questions"
        intermediateTextView.text = ""
        intermediateTextView.text =
            mediumLevel.passedQuestions.toString() + " / 13 passed questions"
        advancedTextView.text = ""
        advancedTextView.text = hardLevel.passedQuestions.toString() + " / 13 passed questions"
    }

    private fun populateRandomWordContainer() {
        CoroutineScope(Dispatchers.IO).launch {
            homeActivityViewModel.getRandomWord()
        }
    }

    private fun showRandomWord(t: RandomWord?) {
        val randomTextView: TextView = findViewById(R.id.randomWordTextView)
        randomTextView.text = ""
        randomTextView.text = t?.word

        val partOfSpeechTextView: TextView = findViewById(R.id.partOfSpeechTextView)
        partOfSpeechTextView.text = ""
        partOfSpeechTextView.text = (t?.definitions?.get(0)?.partOfSpeech ?: "")

        val definitionTextView: TextView = findViewById(R.id.definitionTextView)
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
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkIfLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}