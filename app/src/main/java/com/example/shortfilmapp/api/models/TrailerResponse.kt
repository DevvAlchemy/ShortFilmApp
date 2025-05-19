package com.example.shortfilmapp.api.models

import com.example.shortfilmapp.domain.models.Trailer

data class TrailerResponse(
    val id: Int,
    val results: List<Trailer>
)