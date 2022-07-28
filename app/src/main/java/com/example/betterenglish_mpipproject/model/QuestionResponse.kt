package com.example.betterenglish_mpipproject.model

data class QuestionResponse(var questions: List<Question>? = null,
                            var exception: Exception? = null)
