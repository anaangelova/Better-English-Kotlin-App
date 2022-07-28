package com.example.betterenglish_mpipproject.model

import com.example.betterenglish_mpipproject.enums.Difficulty

data class Attempt(var difficulty: Difficulty, var userId: String, var answersForAttempt: MutableList<Boolean>, var timestamp: Long)
