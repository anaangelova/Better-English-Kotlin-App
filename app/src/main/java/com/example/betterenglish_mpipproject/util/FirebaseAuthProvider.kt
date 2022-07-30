package com.example.betterenglish_mpipproject.util

import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthProvider {

    companion object {
        @Volatile
        private var INSTANCE: FirebaseAuth? = null

        @JvmStatic
        fun getFirebaseAuth(): FirebaseAuth {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = createFirebaseAuth()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private fun createFirebaseAuth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }
    }
}