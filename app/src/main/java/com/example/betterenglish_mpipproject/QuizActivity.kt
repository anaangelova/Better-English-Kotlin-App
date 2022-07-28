package com.example.betterenglish_mpipproject

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.core.view.marginLeft
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.betterenglish_mpipproject.enums.Difficulty
import com.example.betterenglish_mpipproject.model.*
import com.example.betterenglish_mpipproject.viewmodel.QuizActivityViewModel
import org.w3c.dom.Text
import android.widget.LinearLayout




class QuizActivity : AppCompatActivity() {

    private lateinit var quizActivityViewModel: QuizActivityViewModel
    private lateinit var questions: MutableList<Question>
    private lateinit var attempt: Attempt
    private var currentQuestionIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        quizActivityViewModel = ViewModelProvider(this).get(QuizActivityViewModel::class.java)
        questions = mutableListOf()

        val bundle: Bundle? = intent.extras;

        var userId = bundle?.get("userId").toString()
        var index = bundle?.get("level") as Int
        var difficulty: Difficulty = Difficulty.values()[index]
        attempt = Attempt(difficulty, userId, mutableListOf(), System.currentTimeMillis())
        // listata treba da se azhurira za sekoe prashanje

        populateQuestionsList(difficulty)
        var progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.setProgress(0, true)

    }

    private fun checkAnswer() {
        var radioGroup: RadioGroup = findViewById(R.id.answersRadioGroup)
        val checkedAnswerOptionRadioButton: RadioButton =
            findViewById(radioGroup.checkedRadioButtonId)
        val checkedAnswer = checkedAnswerOptionRadioButton.text

        var correctAnswer =
            questions[currentQuestionIndex].answers?.entries?.filter { e -> e.value }
                ?.map { e -> e.key }?.first()
        attempt.answersForAttempt.add(currentQuestionIndex, checkedAnswer == correctAnswer)
        if (checkedAnswer == correctAnswer) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()

        } else {
           Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show()

        }
        var progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.setProgress((progressBar.progress+8), true)
        currentQuestionIndex++
        showNextQuestion()
    }

    private fun showNextQuestion() {
        if (currentQuestionIndex <= questions.size - 1) {
            populateUI()
        } else {
            quizActivityViewModel.addAttempt(attempt)

            //ako ima povekje od 60% tochni - go ima vo correctAnswers, da se azhurira otklucheniot level za korisnikot

            // i na momentalniot level (go imame vo attempt) da se azhurira vrednosta za passedQuestions, go imame vo var correctAnswers
            quizActivityViewModel.getLevelsForUser(attempt.userId).observe(this, {
                var list = it.levels
                var correctAnswers = attempt.answersForAttempt.filter { el -> el }.count()
                var wrongAnswers = attempt.answersForAttempt.size - correctAnswers
                var successRate = ( correctAnswers*1.0 / attempt.answersForAttempt.size ) * 100

                var currentLevel: Level? =
                    list?.last { el -> el.difficulty.toString() == attempt.difficulty.toString() }
                currentLevel?.passedQuestions = correctAnswers
                currentLevel?.isUnlocked = true
                quizActivityViewModel.updateLevelForUser(currentLevel!!)

                var nextLevel: Level? =
                    list?.last { el -> el.difficulty?.ordinal == attempt.difficulty.ordinal+1 }
                if(successRate >= 60) {
                    nextLevel?.isUnlocked=true
                    quizActivityViewModel.updateLevelForUser(nextLevel!!)
                } else {
                    nextLevel?.isUnlocked=false
                    quizActivityViewModel.updateLevelForUser(nextLevel!!)
                }

                val intent = Intent(this, ReportActivity::class.java)
                intent.putExtra("correctAnswers",correctAnswers)
                intent.putExtra("wrongAnswers",wrongAnswers)
                intent.putExtra("successRate",successRate)
                intent.putExtra("levelName",attempt.difficulty.name)
                startActivity(intent)
            })

        }
        var radioGroup: RadioGroup = findViewById(R.id.answersRadioGroup)
        radioGroup.clearCheck()
    }

    private fun populateQuestionsList(difficulty: Difficulty) {
        quizActivityViewModel.getQuestionsSA(difficulty).observe(this, { it ->
            var questionsListSA = it.questions?.shuffled()?.subList(0,5)
            if (questionsListSA != null) {
                questions.addAll(questionsListSA)
            }

            quizActivityViewModel.getSentenceQuestions(difficulty).observe(this, {
                var questionsListSentence = it.questions?.shuffled()?.subList(0,5)
                if (questionsListSentence != null) {
                    questions.addAll(questionsListSentence)
                }
            })

            quizActivityViewModel.getPictureQuestions(difficulty).observe(this, {
                var questionsListPicture = it.questions?.shuffled()?.subList(0,3)
                if (questionsListPicture != null) {
                    questions.addAll(questionsListPicture)
                }
            })

            populateUI()

        })

    }

    private fun populateUI() {

        var linearLayout: LinearLayout = findViewById(R.id.linearLayoutContainer)
        var questionDescription: TextView = findViewById(R.id.questionDescriptionTextView)
        var firstOption: RadioButton = findViewById(R.id.firstOptionRadioButton)
        var secondOption: RadioButton = findViewById(R.id.secondOptionRadioButton)
        var thirdOption: RadioButton = findViewById(R.id.thirdOptionRadioButton)
        var fourthOption: RadioButton = findViewById(R.id.fourthOptionRadioButton)
        var nextButton: Button = findViewById(R.id.nextButton)
        var radioGroup: RadioGroup = findViewById(R.id.answersRadioGroup)

        var currentQuestion: Question
        when (questions[currentQuestionIndex]) {
            is SynonymsAndAntonymsQuestion -> {
                currentQuestion =
                    questions[currentQuestionIndex] as SynonymsAndAntonymsQuestion
                questionDescription.text = currentQuestion.description

                var wordOrSentenceTextView = TextView(this)
                wordOrSentenceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,32F)
                wordOrSentenceTextView.typeface = Typeface.DEFAULT_BOLD

                wordOrSentenceTextView.text = currentQuestion.word
                val params: LinearLayout.LayoutParams =
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(200, 10, 10, 0)
                wordOrSentenceTextView.layoutParams = params
                linearLayout.removeAllViews()
                linearLayout.addView(wordOrSentenceTextView)

                var listOfAnswers: List<String> = currentQuestion.answers?.keys?.toList()?.shuffled()!!
                firstOption.text = listOfAnswers[0]
                secondOption.text = listOfAnswers[1]
                thirdOption.text = listOfAnswers[2]
                fourthOption.text = listOfAnswers[3]

            }
            is SentenceQuestion -> {
                currentQuestion = questions[currentQuestionIndex] as SentenceQuestion
                questionDescription.text = currentQuestion.description

                var wordOrSentenceTextView = TextView(this)
                wordOrSentenceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,32F)
                wordOrSentenceTextView.typeface = Typeface.DEFAULT_BOLD
                val params: LinearLayout.LayoutParams =
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(70, 10, 10, 0)
                wordOrSentenceTextView.layoutParams = params
                wordOrSentenceTextView.text = currentQuestion.sentence

                linearLayout.removeAllViews()
                linearLayout.addView(wordOrSentenceTextView)

                var listOfAnswers: List<String> = currentQuestion.answers?.keys?.toList()?.shuffled()!!
                firstOption.text = listOfAnswers[0]
                secondOption.text = listOfAnswers[1]
                thirdOption.text = listOfAnswers[2]
                fourthOption.text = listOfAnswers[3]
            }
            is PictureQuestion -> {
                currentQuestion = questions[currentQuestionIndex] as PictureQuestion
                questionDescription.text = currentQuestion.description
                var listOfAnswers: List<String> = currentQuestion.answers?.keys?.toList()?.shuffled()!!
                firstOption.text = listOfAnswers[0]
                secondOption.text = listOfAnswers[1]
                thirdOption.text = listOfAnswers[2]
                fourthOption.text = listOfAnswers[3]

                var questionImage = ImageView(this)
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