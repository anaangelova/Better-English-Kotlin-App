package com.example.betterenglish_mpipproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.math.BigDecimal
import java.math.RoundingMode

class ReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val bundle: Bundle? = intent.extras;
        var correctAnswers = bundle?.get("correctAnswers") as Int
        var wrongAnswers = bundle.get("wrongAnswers") as Int
        var successRate = bundle.get("successRate") as Double
        val bd = BigDecimal(successRate)
        val roundoff = bd.setScale(2, RoundingMode.FLOOR)
        var levelName = bundle.get("levelName").toString()


        var levelPlayedTextView: TextView = findViewById(R.id.levelPlayedTextView)
        var correctAnswersTextView: TextView = findViewById(R.id.correctAnswersTextView)
        var wrongAnswersTextView: TextView = findViewById(R.id.wrongAnswersTextView)
        var successRateTextView: TextView = findViewById(R.id.successRateTextView)


        levelPlayedTextView.text = "Level: " + levelName
        correctAnswersTextView.text = "Correct answers: " + correctAnswers.toString()
        wrongAnswersTextView.text = "Wrong answers: " + wrongAnswers.toString()
        successRateTextView.text = "Success rate:  " + roundoff.toString()

        var nextButton: Button = findViewById(R.id.okButton)
        nextButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java);
            startActivity(intent)
            finish();
        }

    }


}