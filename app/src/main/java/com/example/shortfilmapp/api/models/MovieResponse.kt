package com.example.shortfilmapp.api.models

import com.example.shortfilmapp.domain.models.Movie
import com.google.gson.annotations.SerializedName

data class MovieResponse(
    val page: Int,
    val results: List<MovieDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)