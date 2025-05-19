package com.example.shortfilmapp.utils

object ImageUtils {
    private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    private const val POSTER_SIZE = "w500"
    private const val BACKDROP_SIZE = "w1280"

    fun getPosterUrl(posterPath: String?): String {
        return if (posterPath.isNullOrEmpty()) {
            ""
        } else {
            "$IMAGE_BASE_URL$POSTER_SIZE$posterPath"
        }
    }

    fun getBackdropUrl(backdropPath: String?): String {
        return if (backdropPath.isNullOrEmpty()) {
            ""
        } else {
            "$IMAGE_BASE_URL$BACKDROP_SIZE$backdropPath"
        }
    }
}