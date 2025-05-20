package com.example.shortfilmapp.repository

import com.example.shortfilmapp.domain.models.Movie
import com.example.shortfilmapp.domain.models.Trailer

interface MovieRepository {
    suspend fun getPopularMovies(): List<Movie>
    suspend fun getMovieTrailers(movieId: Int): List<Trailer>
    suspend fun searchMovies(query: String): List<Movie>
    suspend fun getTopRatedMovies(): List<Movie>
    suspend fun getUpcomingMovies(): List<Movie>
}

