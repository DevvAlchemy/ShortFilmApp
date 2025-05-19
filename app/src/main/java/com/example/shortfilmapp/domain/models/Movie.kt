package com.example.shortfilmapp.domain.models

// domain/models/Movie.kt
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val releaseDate: String,
    val rating: Double
)