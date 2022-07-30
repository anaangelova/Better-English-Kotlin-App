package com.example.betterenglish_mpipproject.domain.level.model

import com.example.betterenglish_mpipproject.util.Difficulty

data class Level(var difficulty: Difficulty? = null, var userId: String? = null, var isUnlocked: Boolean? = null, var passedQuestions: Int? = null) {
}
