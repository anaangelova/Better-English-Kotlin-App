package com.example.betterenglish_mpipproject.domain.question.model

import com.example.betterenglish_mpipproject.util.Difficulty

data class PictureQuestion(override var description: String? = null, override var difficulty: Difficulty? = null, var pictureURL: String? = null,
                           override var answers: Map<String, Boolean>? = null
) : Question(){

}