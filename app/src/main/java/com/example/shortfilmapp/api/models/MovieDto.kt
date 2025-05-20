package com.example.shortfilmapp.api.models

import com.google.gson.annotations.SerializedName

data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,    // Keep this as poster_path to match API
    @SerializedName("backdrop_path")   val backdropPath: String?,  // Keep this as backdrop_path to match API
    @SerializedName("release_date") val  releaseDate: String,    // Keep this as release_date to match API
    @SerializedName("vote_average") val voteAverage: Double     // Keep this as vote_average to match API
)