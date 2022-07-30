package com.example.betterenglish_mpipproject.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class WordsApiClient {

    companion object {
        private var wordsApi: WordsApi? = null

        fun getWordsApi(): WordsApi? {
            if (wordsApi == null) {
                wordsApi = Retrofit.Builder().baseUrl("https://wordsapiv1.p.rapidapi.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(WordsApi::class.java)
            }

            return wordsApi
        }
    }
}