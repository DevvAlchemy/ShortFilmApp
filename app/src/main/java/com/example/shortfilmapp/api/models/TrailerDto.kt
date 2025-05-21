package com.example.shortfilmapp.api.models

import com.google.gson.annotations.SerializedName

data class TrailerDto(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val size: Int,
    val type: String,
    @SerializedName("published_at") val publishedAt: String
)