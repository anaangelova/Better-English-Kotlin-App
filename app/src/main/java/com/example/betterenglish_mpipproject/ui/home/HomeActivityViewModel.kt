package com.example.betterenglish_mpipproject.ui.home

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.betterenglish_mpipproject.domain.level.model.Level
import com.example.betterenglish_mpipproject.domain.word.RandomWord
import com.example.betterenglish_mpipproject.domain.word.SearchQueryWordResult
import com.example.betterenglish_mpipproject.domain.level.LevelRepository
import com.example.betterenglish_mpipproject.data.WordsApi
import com.example.betterenglish_mpipproject.data.WordsApiClient
import com.example.betterenglish_mpipproject.domain.level.model.LevelResponse
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

    fun getLevelsForUser(userId: String): LiveData<LevelResponse> {
        return levelRepository.getLevelsForUser(userId)
    }
    fun addLevel(level: Level) {
        levelRepository.addLevelToDatabase(level)
    }

    fun getRandomWord() {
        val page: Int = getRandomPage()
        var pickedWord: String
        wordsApi.getWordsFromSearch("^([^0-9]*)\$", "definitions", page)
            .enqueue(object : Callback<SearchQueryWordResult> {
                override fun onResponse(
                    call: Call<SearchQueryWordResult>,
                    response: Response<SearchQueryWordResult>
                ) {
                    if (response.body() != null) {
                        val wordResult: SearchQueryWordResult = response.body()!!
                        val listOfWords: MutableList<String> = wordResult.results.data
                        pickedWord = listOfWords[Random.nextInt(0,listOfWords.size)]
                        getDefinitionForRandomWord(pickedWord)
                    }
                }

                override fun onFailure(call: Call<SearchQueryWordResult>, t: Throwable) {
                    Toast.makeText(app, "Error!", Toast.LENGTH_LONG).show()
                }
            })

    }

    private fun getDefinitionForRandomWord(pickedWord: String) {
        wordsApi.getDefinitionForWord(pickedWord).enqueue(object : Callback<RandomWord>
        {
            override fun onResponse(call: Call<RandomWord>, response: Response<RandomWord>) {
                if(response.body() != null) {
                    val result: RandomWord = response.body()!!
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