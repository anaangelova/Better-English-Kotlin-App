package com.example.betterenglish_mpipproject.domain.attempt.model

import com.example.betterenglish_mpipproject.util.AttemptStatus
import com.example.betterenglish_mpipproject.util.Difficulty

data class Attempt(
    var difficulty: Difficulty,
    var userId: String,
    var answersForAttempt: MutableList<Boolean>,
    var timestamp: Long,
    var status: AttemptStatus
)
