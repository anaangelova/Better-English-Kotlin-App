package com.example.betterenglish_mpipproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.betterenglish_mpipproject.enums.Difficulty
import com.example.betterenglish_mpipproject.model.Attempt
import com.example.betterenglish_mpipproject.model.Level
import com.example.betterenglish_mpipproject.model.QuestionResponse
import com.example.betterenglish_mpipproject.model.Response
import com.example.betterenglish_mpipproject.repository.AttemptRepository
import com.example.betterenglish_mpipproject.repository.LevelRepository
import com.example.betterenglish_mpipproject.repository.QuestionRepository

class QuizActivityViewModel(application: Application) : AndroidViewModel(application)  {

    private val questionRepository: QuestionRepository = QuestionRepository()
    private val levelRepository: LevelRepository = LevelRepository()
    private val attemptRepository: AttemptRepository = AttemptRepository()
    private var app: Application

    init {
        app = application
    }


    fun getQuestionsSA(difficulty: Difficulty): MutableLiveData<QuestionResponse> {
        return questionRepository.getQuestionsFromDatabase(difficulty)
    }
    fun getSentenceQuestions(difficulty: Difficulty): MutableLiveData<QuestionResponse> {
        return questionRepository.getSentenceQuestionsFromDatabase(difficulty)
    }
    fun getPictureQuestions(difficulty: Difficulty): MutableLiveData<QuestionResponse> {
        return questionRepository.getPictureQuestionsFromDatabase(difficulty)
    }

    fun addAttempt(attempt: Attempt) {
        attemptRepository.addAttemptToDatabase(attempt)
    }

    fun getLevelsForUser(userId: String): LiveData<Response> {
        return levelRepository.getLevelsForUser(userId)
    }

    fun updateLevelForUser(level: Level) {
        levelRepository.addLevelToDatabase(level)
    }
}