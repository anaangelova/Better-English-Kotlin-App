package com.example.betterenglish_mpipproject.domain.question

import androidx.lifecycle.MutableLiveData
import com.example.betterenglish_mpipproject.domain.question.model.*
import com.example.betterenglish_mpipproject.util.Difficulty
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class QuestionRepository(
    private val rootRef: DatabaseReference =  FirebaseDatabase.getInstance("https://better-english-c10b3-default-rtdb.firebaseio.com").reference,
    private val questionsRef: DatabaseReference = rootRef.child("questions"),
    private val questionsWithPictureRef: DatabaseReference = rootRef.child("questionsWithPicture"),
    private val sentenceQuestionsRef: DatabaseReference = rootRef.child("sentenceQuestions")
) {

    fun getSentenceQuestionsFromDatabase(difficulty: Difficulty): MutableLiveData<QuestionResponse> {
        val name = difficulty.name
        val mutableLiveData = MutableLiveData<QuestionResponse>()
        sentenceQuestionsRef.orderByChild("difficulty").equalTo(name)
            .get().addOnCompleteListener {
                    task ->
                val response = QuestionResponse()
                if (task.isSuccessful) {
                    val result = task.result
                    result?.let {
                        response.questions = result.children.map { snapShot ->
                            snapShot.getValue(SentenceQuestion::class.java)!!
                        }
                    }
                } else {
                    response.exception = task.exception
                }
                mutableLiveData.value = response
            }

        return mutableLiveData
    }

    fun getQuestionsFromDatabase(difficulty: Difficulty): MutableLiveData<QuestionResponse>  {
        val name = difficulty.name
        val mutableLiveData = MutableLiveData<QuestionResponse>()
        questionsRef.orderByChild("difficulty").equalTo(name)
            .get().addOnCompleteListener {
                    task ->
                val response = QuestionResponse()
                if (task.isSuccessful) {
                    val result = task.result
                    result?.let {
                        response.questions = result.children.map { snapShot ->
                            snapShot.getValue(SynonymsAndAntonymsQuestion::class.java)!!
                        }
                    }
                } else {
                    response.exception = task.exception
                }
                mutableLiveData.value = response
            }

        return mutableLiveData
    }

    fun getPictureQuestionsFromDatabase(difficulty: Difficulty): MutableLiveData<QuestionResponse>  {
        val name = difficulty.name
        val mutableLiveData = MutableLiveData<QuestionResponse>()
        questionsWithPictureRef.orderByChild("difficulty").equalTo(name)
            .get().addOnCompleteListener {
                    task ->
                val response = QuestionResponse()
                if (task.isSuccessful) {
                    val result = task.result
                    result?.let {
                        response.questions = result.children.map { snapShot ->
                            snapShot.getValue(PictureQuestion::class.java)!!
                        }
                    }
                } else {
                    response.exception = task.exception
                }
                mutableLiveData.value = response
            }
        return mutableLiveData
    }

    fun addQuestionToDatabase(question: Question) {
        questionsRef.push().setValue(question)
    }

}