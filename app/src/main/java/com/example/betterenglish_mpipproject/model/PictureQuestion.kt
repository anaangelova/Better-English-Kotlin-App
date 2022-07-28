package com.example.betterenglish_mpipproject.model

import com.example.betterenglish_mpipproject.enums.Difficulty

data class PictureQuestion(override var description: String? = null, override var difficulty: Difficulty? = null, var pictureURL: String? = null,
                           override var answers: Map<String, Boolean>? = null
) : Question(){

}