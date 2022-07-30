package com.example.betterenglish_mpipproject.domain.question.model

import com.example.betterenglish_mpipproject.util.Difficulty

data class SynonymsAndAntonymsQuestion(override var description: String? = null, override var difficulty: Difficulty? = null, override var answers: Map<String, Boolean>? = null, var word: String? = null) : Question() {

}