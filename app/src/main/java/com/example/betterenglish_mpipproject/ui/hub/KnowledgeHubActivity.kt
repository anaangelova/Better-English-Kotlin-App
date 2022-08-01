package com.example.betterenglish_mpipproject.ui.hub

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.betterenglish_mpipproject.R
import com.example.betterenglish_mpipproject.ui.vocabulary.VocabularyActivity

class KnowledgeHubActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_knowledge_hub)

        val learnGrammarButton: Button = findViewById(R.id.learnGrammarButton)
        val learnVocabularyButton: Button = findViewById(R.id.learnVocabularyButton)
        val learnListeningButton: Button = findViewById(R.id.learnListeningButton)


        learnGrammarButton.setOnClickListener {
            openInBrowser("https://learnenglish.britishcouncil.org/grammar")
        }

        learnVocabularyButton.setOnClickListener {
            val intent = Intent(this, VocabularyActivity::class.java)
            startActivity(intent)
        }

        learnListeningButton.setOnClickListener {
            openInBrowser("https://learnenglish.britishcouncil.org/skills/listening")
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right)
    }

    private fun openInBrowser(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(intent, "Browse with"))
        }
    }


}