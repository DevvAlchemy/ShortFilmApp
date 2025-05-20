package com.example.shortfilmapp.api.models

data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,    // Keep this as poster_path to match API
    val backdropPath: String?,  // Keep this as backdrop_path to match API
    val releaseDate: String,    // Keep this as release_date to match API
    val voteAverage: Double     // Keep this as vote_average to match API
)