package com.example.betterenglish_mpipproject.wordsApi

import com.example.betterenglish_mpipproject.model.RandomWord
import com.example.betterenglish_mpipproject.model.SearchQueryResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface WordsApi {

    @GET("words/")
    @Headers(
        "X-RapidAPI-Key: d24eb842a9msh2ad7f705f8e050ap113542jsn9dba58eea985",
        "X-RapidAPI-Host: wordsapiv1.p.rapidapi.com"
    )
    fun getWordsFromSearch(@Query("letterPattern") letterPattern: String, @Query("hasDetails") hasDetails: String,
                           @Query("page") page: Int): Call<SearchQueryResult>


    @GET("words/{word}/definitions")
    @Headers(
        "X-RapidAPI-Key: d24eb842a9msh2ad7f705f8e050ap113542jsn9dba58eea985",
        "X-RapidAPI-Host: wordsapiv1.p.rapidapi.com"
    )
    fun getDefinitionForWord(@Path("word") word: String): Call<RandomWord>




}