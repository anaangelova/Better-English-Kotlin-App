package com.example.betterenglish_mpipproject.ui.quiz

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.betterenglish_mpipproject.util.Difficulty
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.example.betterenglish_mpipproject.R
import com.example.betterenglish_mpipproject.ui.report.ReportActivity
import com.example.betterenglish_mpipproject.domain.attempt.model.Attempt
import com.example.betterenglish_mpipproject.domain.level.model.Level
import com.example.betterenglish_mpipproject.domain.question.model.PictureQuestion
import com.example.betterenglish_mpipproject.domain.question.model.Question
import com.example.betterenglish_mpipproject.domain.question.model.SentenceQuestion
import com.example.betterenglish_mpipproject.domain.question.model.SynonymsAndAntonymsQuestion
import com.example.betterenglish_mpipproject.ui.home.HomeActivity
import com.example.betterenglish_mpipproject.util.AttemptStatus
import java.util.*

class QuizActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var quizActivityViewModel: QuizActivityViewModel
    private lateinit var questions: MutableList<Question>
    private lateinit var attempt: Attempt
    private var currentQuestionIndex: Int = 0
    private var textToSpeechButton: ImageView? = null
    private var ttsComponent: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        quizActivityViewModel = ViewModelProvider(this).get(QuizActivityViewModel::class.java)
        questions = mutableListOf()

        val bundle: Bundle? = intent.extras
        val userId = bundle?.get("userId").toString()
        val index = bundle?.get("level") as Int
        val difficulty: Difficulty = Difficulty.values()[index]
        attempt = Attempt(
            difficulty,
            userId,
            mutableListOf(),
            System.currentTimeMillis(),
            AttemptStatus.STARTED
        )

        populateQuestionsList(difficulty)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.setProgress(0, true)

        textToSpeechButton = ImageView(this)
        textToSpeechButton!!.load("https://i.ibb.co/7N78dTT/speaker.png")
        textToSpeechButton!!.isEnabled = false
        ttsComponent = TextToSpeech(this, this)
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        val view =
            LayoutInflater.from(this).inflate(R.layout.quit_quiz_alert_dialog, null)
        builder.setView(view)

        val quitQuizButton: Button = view.findViewById(R.id.quitQuizButton)
        val dismissButton: Button = view.findViewById(R.id.dismissButton)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        quitQuizButton.setOnClickListener {
            attempt.status = AttemptStatus.CANCELLED
            quizActivityViewModel.addAttempt(attempt)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        dismissButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = ttsComponent!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language not supported!")
                textToSpeechButton!!.isEnabled = true
            } else {
                textToSpeechButton!!.isEnabled = true
            }
        }
    }

    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        if (ttsComponent != null) {
            ttsComponent!!.stop()
            ttsComponent!!.shutdown()
        }
        super.onDestroy()
    }

    private fun checkAnswer() {
        val radioGroup: RadioGroup = findViewById(R.id.answersRadioGroup)
        val checkedAnswerOptionRadioButton: RadioButton =
            findViewById(radioGroup.checkedRadioButtonId)
        val checkedAnswer = checkedAnswerOptionRadioButton.text

        val correctAnswer =
            questions[currentQuestionIndex].answers?.entries?.filter { e -> e.value }
                ?.map { e -> e.key }?.first()
        attempt.answersForAttempt.add(currentQuestionIndex, checkedAnswer == correctAnswer)

        if (checkedAnswer == correctAnswer) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show()
        }

        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.setProgress((progressBar.progress + 8), true)
        currentQuestionIndex++
        showNextQuestion()
    }

    private fun showNextQuestion() {
        if (currentQuestionIndex <= questions.size - 1) {
            populateUI()
        } else {
            attempt.status = AttemptStatus.FINISHED
            quizActivityViewModel.addAttempt(attempt)

            quizActivityViewModel.getLevelsForUser(attempt.userId).observe(this, {
                val levelsForUser = it.levels
                val correctAnswers = attempt.answersForAttempt.filter { el -> el }.count()
                val wrongAnswers = attempt.answersForAttempt.size - correctAnswers
                val successRate = (correctAnswers * 1.0 / attempt.answersForAttempt.size) * 100

                val currentLevel: Level? =
                    levelsForUser?.last { el -> el.difficulty.toString() == attempt.difficulty.toString() }
                currentLevel?.passedQuestions = correctAnswers
                currentLevel?.isUnlocked = true
                quizActivityViewModel.updateLevelForUser(currentLevel!!)

                if (attempt.difficulty.name != "ADVANCED") {
                    val nextLevel: Level =
                        levelsForUser.last { el -> el.difficulty?.ordinal == attempt.difficulty.ordinal + 1 }
                    if (successRate >= 60) {
                        nextLevel.isUnlocked = true
                        quizActivityViewModel.updateLevelForUser(nextLevel)
                    } else {
                        nextLevel.isUnlocked = false
                        nextLevel.passedQuestions = 0
                        quizActivityViewModel.updateLevelForUser(nextLevel)
                    }
                }

                val intent = Intent(this, ReportActivity::class.java)
                intent.putExtra("correctAnswers", correctAnswers)
                intent.putExtra("wrongAnswers", wrongAnswers)
                intent.putExtra("successRate", successRate)
                intent.putExtra("levelName", attempt.difficulty.name)
                startActivity(intent)
                finish()
            })

        }
        val radioGroup: RadioGroup = findViewById(R.id.answersRadioGroup)
        radioGroup.clearCheck()
    }

    private fun populateQuestionsList(difficulty: Difficulty) {
        quizActivityViewModel.getQuestionsSA(difficulty).observe(this, { it ->
            val questionsListSA = it.questions?.shuffled()?.subList(0, 5)
            if (questionsListSA != null) {
                questions.addAll(questionsListSA)
            }

            quizActivityViewModel.getSentenceQuestions(difficulty).observe(this, {
                val questionsListSentence = it.questions?.shuffled()?.subList(0, 5)
                if (questionsListSentence != null) {
                    questions.addAll(questionsListSentence)
                }
            })

            quizActivityViewModel.getPictureQuestions(difficulty).observe(this, {
                val questionsListPicture = it.questions?.shuffled()?.subList(0, 3)
                if (questionsListPicture != null) {
                    questions.addAll(questionsListPicture)
                }
            })

            populateUI()
        })

    }

    private fun populateUI() {

        val linearLayout: LinearLayout = findViewById(R.id.linearLayoutContainer)
        val questionDescription: TextView = findViewById(R.id.questionDescriptionTextView)
        val firstOption: RadioButton = findViewById(R.id.firstOptionRadioButton)
        val secondOption: RadioButton = findViewById(R.id.secondOptionRadioButton)
        val thirdOption: RadioButton = findViewById(R.id.thirdOptionRadioButton)
        val fourthOption: RadioButton = findViewById(R.id.fourthOptionRadioButton)
        val nextButton: Button = findViewById(R.id.nextButton)
        val radioGroup: RadioGroup = findViewById(R.id.answersRadioGroup)

        val currentQuestion: Question
        when (questions[currentQuestionIndex]) {
            is SynonymsAndAntonymsQuestion -> {
                currentQuestion =
                    questions[currentQuestionIndex] as SynonymsAndAntonymsQuestion
                questionDescription.text = currentQuestion.description

                val wordOrSentenceTextView = TextView(this)
                wordOrSentenceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42F)
                wordOrSentenceTextView.typeface = Typeface.DEFAULT_BOLD

                val word: String = currentQuestion.word!!
                wordOrSentenceTextView.text = word.uppercase()
                wordOrSentenceTextView.gravity = Gravity.CENTER
                wordOrSentenceTextView.setTextColor(Color.parseColor("#1565C0"))

                textToSpeechButton?.setOnClickListener {
                    ttsComponent?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "")
                }

                linearLayout.removeAllViews()
                linearLayout.addView(wordOrSentenceTextView)
                linearLayout.addView(textToSpeechButton)

                val listOfAnswers: List<String> =
                    currentQuestion.answers?.keys?.toList()?.shuffled()!!
                firstOption.text = listOfAnswers[0]
                secondOption.text = listOfAnswers[1]
                thirdOption.text = listOfAnswers[2]
                fourthOption.text = listOfAnswers[3]

            }
            is SentenceQuestion -> {
                currentQuestion = questions[currentQuestionIndex] as SentenceQuestion
                questionDescription.text = currentQuestion.description

                val wordOrSentenceTextView = TextView(this)
                wordOrSentenceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32F)
                wordOrSentenceTextView.typeface = Typeface.DEFAULT_BOLD
                wordOrSentenceTextView.gravity = Gravity.CENTER
                wordOrSentenceTextView.setTextColor(Color.parseColor("#1565C0"))
                wordOrSentenceTextView.text = currentQuestion.sentence

                linearLayout.removeAllViews()
                linearLayout.addView(wordOrSentenceTextView)

                val listOfAnswers: List<String> =
                    currentQuestion.answers?.keys?.toList()?.shuffled()!!
                firstOption.text = listOfAnswers[0]
                secondOption.text = listOfAnswers[1]
                thirdOption.text = listOfAnswers[2]
                fourthOption.text = listOfAnswers[3]
            }
            is PictureQuestion -> {
                currentQuestion = questions[currentQuestionIndex] as PictureQuestion
                questionDescription.text = currentQuestion.description
                val listOfAnswers: List<String> =
                    currentQuestion.answers?.keys?.toList()?.shuffled()!!
                firstOption.text = listOfAnswers[0]
                secondOption.text = listOfAnswers[1]
                thirdOption.text = listOfAnswers[2]
                fourthOption.text = listOfAnswers[3]

                val questionImage = ImageView(this)
                questionImage.load(currentQuestion.pictureURL)
                linearLayout.removeAllViews()
                linearLayout.addView(questionImage)
            }

        }
        nextButton.setOnClickListener {
            if (radioGroup.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
            } else {
                checkAnswer()
            }
        }
    }


}