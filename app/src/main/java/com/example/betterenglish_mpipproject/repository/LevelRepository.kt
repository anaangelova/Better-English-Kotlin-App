package com.example.betterenglish_mpipproject.repository

import androidx.lifecycle.MutableLiveData
import com.example.betterenglish_mpipproject.model.Level
import com.example.betterenglish_mpipproject.model.Response
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LevelRepository(
    private val rootRef: DatabaseReference =  FirebaseDatabase.getInstance("https://better-english-c10b3-default-rtdb.firebaseio.com").reference,
    private val levelsRef: DatabaseReference = rootRef.child("levels")
) {

    fun getLevels(): MutableLiveData<Response> {
        val mutableLiveData = MutableLiveData<Response>()
        levelsRef.get().addOnCompleteListener { task ->
            val response = Response()
            if (task.isSuccessful) {
                val result = task.result
                result?.let {
                    response.levels = result.children.map { snapShot ->
                        snapShot.getValue(Level::class.java)!!
                    }
                }
            } else {
                response.exception = task.exception
            }
            mutableLiveData.value = response
        }
        return mutableLiveData
    }

    fun getLevelsForUser(userId: String): MutableLiveData<Response> {
        val mutableLiveData = MutableLiveData<Response>()
        levelsRef.orderByChild("userId").equalTo(userId)
            .get().addOnCompleteListener {
                task ->
            val response = Response()
            if (task.isSuccessful) {
                val result = task.result
                result?.let {
                    response.levels = result.children.map { snapShot ->
                        snapShot.getValue(Level::class.java)!!
                    }
                }
            } else {
                response.exception = task.exception
            }
            mutableLiveData.value = response
        }

        return mutableLiveData
    }

    fun addLevelToDatabase(level: Level) {
        levelsRef.push().setValue(level)
    }
}