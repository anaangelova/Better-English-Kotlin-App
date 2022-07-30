package com.example.betterenglish_mpipproject.domain.level

import androidx.lifecycle.MutableLiveData
import com.example.betterenglish_mpipproject.domain.level.model.LevelResponse
import com.example.betterenglish_mpipproject.domain.level.model.Level
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LevelRepository(
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance("https://better-english-c10b3-default-rtdb.firebaseio.com").reference,
    private val levelsRef: DatabaseReference = rootRef.child("levels")
) {


    fun getLevelsForUser(userId: String): MutableLiveData<LevelResponse> {
        val mutableLiveData = MutableLiveData<LevelResponse>()
        levelsRef.orderByChild("userId").equalTo(userId)
            .get().addOnCompleteListener { task ->
                val response = LevelResponse()
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

    fun updateLevelForUser(level: Level) {
        levelsRef.setValue(level)
    }
}