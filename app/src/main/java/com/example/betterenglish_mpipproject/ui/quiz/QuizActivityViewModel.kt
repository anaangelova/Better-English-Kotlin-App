package com.example.betterenglish_mpipproject.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.betterenglish_mpipproject.util.Difficulty
import com.example.betterenglish_mpipproject.domain.attempt.model.Attempt
import com.example.betterenglish_mpipproject.domain.level.model.Level
import com.example.betterenglish_mpipproject.domain.question.model.QuestionResponse
import com.example.betterenglish_mpipproject.domain.level.model.LevelResponse
import com.example.betterenglish_mpipproject.domain.attempt.AttemptRepository
import com.example.betterenglish_mpipproject.domain.level.LevelRepository
import com.example.betterenglish_mpipproject.domain.question.QuestionRepository

class QuizActivityViewModel(application: Application) : AndroidViewModel(application)  {

    private val questionRepository: QuestionRepository = QuestionRepository()
    private val levelRepository: LevelRepository = LevelRepository()
    private val attemptRepository: AttemptRepository = AttemptRepository()

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

    fun getLevelsForUser(userId: String): LiveData<LevelResponse> {
        return levelRepository.getLevelsForUser(userId)
    }

    fun updateLevelForUser(level: Level) {
        levelRepository.addLevelToDatabase(level)
    }
}