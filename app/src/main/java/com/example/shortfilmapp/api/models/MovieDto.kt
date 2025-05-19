package com.example.shortfilmapp.api.models

data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,    // Keep this as poster_path to match API
    val backdrop_path: String?,  // Keep this as backdrop_path to match API
    val release_date: String,    // Keep this as release_date to match API
    val vote_average: Double     // Keep this as vote_average to match API
)