package com.example.betterenglish_mpipproject.model

import com.example.betterenglish_mpipproject.enums.Difficulty

data class SynonymsAndAntonymsQuestion(override var description: String? = null, override var difficulty: Difficulty? = null, override var answers: Map<String, Boolean>? = null, var word: String? = null) : Question() {

}