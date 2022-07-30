package com.example.betterenglish_mpipproject.domain.word

data class RandomWord (var word: String, var definitions: MutableList<WordDescription> = mutableListOf())