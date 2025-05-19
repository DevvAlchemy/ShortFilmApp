package com.example.shortfilmapp.api.models

import com.example.shortfilmapp.domain.models.Movie

data class MovieResponse(
    val page: Int,
    val results: List<Movie>,
    val total_pages: Int,
    val total_results: Int
)