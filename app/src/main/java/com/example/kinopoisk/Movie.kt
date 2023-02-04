package com.example.kinopoisk

import java.net.URL

data class Movie(
    val id: Int,
    val name: String,
    val year: String,
    val genre: String,
    val posterURL: URL
)
