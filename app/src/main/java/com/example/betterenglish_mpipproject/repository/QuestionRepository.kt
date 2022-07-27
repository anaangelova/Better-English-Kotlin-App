package com.example.betterenglish_mpipproject.repository

import com.example.betterenglish_mpipproject.model.Question
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class QuestionRepository(
    private val rootRef: DatabaseReference =  FirebaseDatabase.getInstance("https://better-english-c10b3-default-rtdb.firebaseio.com").reference,
    private val questionsRef: DatabaseReference = rootRef.child("questions")
) {

    fun addQuestionToDatabase(question: Question) {
        questionsRef.push().setValue(question)
    }
}