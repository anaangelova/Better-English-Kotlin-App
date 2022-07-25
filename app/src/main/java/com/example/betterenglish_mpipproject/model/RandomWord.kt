package com.example.betterenglish_mpipproject.model

data class RandomWord (var word: String, var definitions: MutableList<WordDescription> = mutableListOf())