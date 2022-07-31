package com.example.betterenglish_mpipproject.ui.vocabulary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.example.betterenglish_mpipproject.R

class VocabularyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocabulary)

        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.loadUrl("https://www.englishfancy.com/100-advanced-english-words-with-meaning/")
    }
}