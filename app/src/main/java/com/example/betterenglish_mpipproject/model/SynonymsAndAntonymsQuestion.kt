package com.example.betterenglish_mpipproject.model

import com.example.betterenglish_mpipproject.enums.Difficulty

data class SynonymsAndAntonymsQuestion(override var description: String, override var difficulty: Difficulty, override var answers: Map<String, Boolean>, var word: String) : Question() {

}