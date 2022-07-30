package com.example.betterenglish_mpipproject.domain.question.model

import com.example.betterenglish_mpipproject.util.Difficulty

data class SentenceQuestion(override var description: String? = null, override var difficulty: Difficulty? = null, var sentence: String? = null,
                            override var answers: Map<String, Boolean>? = null
) : Question(){

}