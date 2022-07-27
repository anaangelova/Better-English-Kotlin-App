package com.example.betterenglish_mpipproject.model

import com.example.betterenglish_mpipproject.enums.Difficulty

abstract class Question {
    abstract val description: String
    abstract val difficulty: Difficulty
    abstract val answers: Map<String, Boolean>
}