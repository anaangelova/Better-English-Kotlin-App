package com.example.betterenglish_mpipproject.data

import com.example.betterenglish_mpipproject.domain.word.RandomWord
import com.example.betterenglish_mpipproject.domain.word.SearchQueryWordResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WordsApi {

    @GET("words/")
    fun getWordsFromSearch(@Query("letterPattern") letterPattern: String, @Query("hasDetails") hasDetails: String,
                           @Query("page") page: Int): Call<SearchQueryWordResult>


    @GET("words/{word}/definitions")
    fun getDefinitionForWord(@Path("word") word: String): Call<RandomWord>




}