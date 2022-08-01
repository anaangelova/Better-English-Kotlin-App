package com.example.betterenglish_mpipproject.ui.report

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.betterenglish_mpipproject.R
import com.example.betterenglish_mpipproject.ui.home.HomeActivity
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

class ReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val bundle: Bundle? = intent.extras
        val correctAnswers = bundle?.get("correctAnswers") as Int
        val wrongAnswers = bundle.get("wrongAnswers") as Int
        val successRate = bundle.get("successRate") as Double
        val bd = BigDecimal(successRate)
        val round = bd.setScale(2, RoundingMode.FLOOR)
        val levelName = bundle.get("levelName").toString()


        val levelPlayedTextView: TextView = findViewById(R.id.levelPlayedTextView)
        val correctAnswersTextView: TextView = findViewById(R.id.correctAnswersTextView)
        val wrongAnswersTextView: TextView = findViewById(R.id.wrongAnswersTextView)
        val successRateTextView: TextView = findViewById(R.id.successRateTextView)


        levelPlayedTextView.text = "Level: " + levelName
        correctAnswersTextView.text = "Correct answers: " + correctAnswers.toString()
        wrongAnswersTextView.text = "Wrong answers: " + wrongAnswers.toString()
        successRateTextView.text = "Success rate:  " + round.toString() + "%"

        val nextButton: Button = findViewById(R.id.okButton)
        nextButton.setOnClickListener {

            if (successRate > 60.0 && (levelName == "BEGINNER" || levelName == "INTERMEDIATE")) {
                val builder = AlertDialog.Builder(this)
                val view =
                    LayoutInflater.from(this).inflate(R.layout.unlocked_level_alert_dialog, null)
                builder.setView(view)

                val closeButton: ImageView = view.findViewById(R.id.closeButton2)
                val dialog = builder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()

                val viewKonfetti: KonfettiView? = dialog.findViewById(R.id.konfettiView)
                viewKonfetti?.start(
                    Party(
                        emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(60),
                    )
                )

                closeButton.setOnClickListener {
                    dialog.dismiss()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                }
            } else {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            }

        }

    }

}