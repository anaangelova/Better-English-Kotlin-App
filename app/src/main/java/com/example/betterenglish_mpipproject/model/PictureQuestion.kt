package com.example.betterenglish_mpipproject.model

import com.example.betterenglish_mpipproject.enums.Difficulty

data class PictureQuestion(override var description: String, override var difficulty: Difficulty, var pictureURL: String,
                           override var answers: Map<String, Boolean>
) : Question(){

}