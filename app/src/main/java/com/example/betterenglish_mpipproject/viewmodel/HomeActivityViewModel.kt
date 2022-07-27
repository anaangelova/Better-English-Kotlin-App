package com.example.betterenglish_mpipproject.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.betterenglish_mpipproject.model.Level
import com.example.betterenglish_mpipproject.model.RandomWord
import com.example.betterenglish_mpipproject.model.SearchQueryResult
import com.example.betterenglish_mpipproject.repository.LevelRepository
import com.example.betterenglish_mpipproject.wordsApi.WordsApi
import com.example.betterenglish_mpipproject.wordsApi.WordsApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class HomeActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var wordsApi: WordsApi
    private var app: Application
    private var wordLiveData: MutableLiveData<RandomWord>
    private val levelRepository: LevelRepository = LevelRepository()

    init {
        wordsApi = WordsApiClient.getWordsApi()!!
        app = application
        wordLiveData = MutableLiveData()
    }

    fun getLevels(): LiveData<com.example.betterenglish_mpipproject.model.Response> {
        return levelRepository.getLevels()
    }

    fun getLevelsForUser(userId: String): LiveData<com.example.betterenglish_mpipproject.model.Response> {
        return levelRepository.getLevelsForUser(userId)
    }
    fun addLevel(level: Level) {
        levelRepository.addLevelToDatabase(level)
    }

    fun getRandomWord() {
        val page: Int = getRandomPage()
        var pickedWord: String
        wordsApi.getWordsFromSearch("^([^0-9]*)\$", "definitions", page)
            .enqueue(object : Callback<SearchQueryResult> {
                override fun onResponse(
                    call: Call<SearchQueryResult>,
                    response: Response<SearchQueryResult>
                ) {
                    if (response.body() != null) {
                        var result: SearchQueryResult = response.body()!!
                        var listOfWords: MutableList<String> = result.results.data
                        pickedWord = listOfWords[Random.nextInt(0,listOfWords.size)]
                        getDefinitionForRandomWord(pickedWord)
                    }
                }

                override fun onFailure(call: Call<SearchQueryResult>, t: Throwable) {
                    Toast.makeText(app, "Error!", Toast.LENGTH_LONG).show()
                }
            })

    }

    private fun getDefinitionForRandomWord(pickedWord: String) {
        wordsApi.getDefinitionForWord(pickedWord).enqueue(object : Callback<RandomWord>
        {
            override fun onResponse(call: Call<RandomWord>, response: Response<RandomWord>) {
                if(response.body() != null) {
                    var result: RandomWord = response.body()!!
                    wordLiveData.value = result
                }
            }

            override fun onFailure(call: Call<RandomWord>, t: Throwable) {
                Toast.makeText(app, "Error!", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun getRandomPage(): Int {
        return Random.nextInt(1, 1470)
    }

    fun getWordLiveData(): MutableLiveData<RandomWord> {
        return wordLiveData
    }
}