package com.example.betterenglish_mpipproject.model

import com.example.betterenglish_mpipproject.enums.Difficulty

data class Level(var difficulty: Difficulty? = null, var userId: String? = null, var isUnlocked: Boolean? = null, var passedQuestions: Int? = null) {
}
