package com.example.betterenglish_mpipproject.domain.question.model

data class QuestionResponse(var questions: List<Question>? = null,
                            var exception: Exception? = null)
