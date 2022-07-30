package com.example.betterenglish_mpipproject.domain.attempt

import androidx.lifecycle.MutableLiveData
import com.example.betterenglish_mpipproject.domain.attempt.model.Attempt
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AttemptRepository(
    private val rootRef: DatabaseReference =  FirebaseDatabase.getInstance("https://better-english-c10b3-default-rtdb.firebaseio.com").reference,
    private val attemptsRef: DatabaseReference = rootRef.child("attempts")
) {

    fun addAttemptToDatabase(attempt: Attempt) {
        attemptsRef.push().setValue(attempt)
    }

    fun getAttemptsForUser(userId: String): MutableLiveData<List<Attempt>> {
        val mutableLiveData = MutableLiveData<List<Attempt>>()
        attemptsRef.orderByChild("userId").equalTo(userId)
            .get().addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    val result = task.result
                    result?.let {
                        mutableLiveData.value = result.children.map { snapShot ->
                            snapShot.getValue(Attempt::class.java)!!
                        }
                    }
                }
            }

        return mutableLiveData
    }


}