package com.example.betterenglish_mpipproject.repository

import com.example.betterenglish_mpipproject.model.Response

interface FirebaseCallback {
    fun onResponse(response: Response)
}