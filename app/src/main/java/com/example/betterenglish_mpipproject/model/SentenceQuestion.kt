package com.example.betterenglish_mpipproject.model

import com.example.betterenglish_mpipproject.enums.Difficulty

data class SentenceQuestion(override var description: String? = null, override var difficulty: Difficulty? = null, var sentence: String? = null,
                            override var answers: Map<String, Boolean>? = null
) : Question(){

}