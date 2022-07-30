package com.example.betterenglish_mpipproject.domain.question.model

import com.example.betterenglish_mpipproject.util.Difficulty

abstract class Question {
    abstract val description: String?
    abstract val difficulty: Difficulty?
    abstract val answers: Map<String, Boolean>?
}