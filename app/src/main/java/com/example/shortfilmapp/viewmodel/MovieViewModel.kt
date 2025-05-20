package com.example.shortfilmapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shortfilmapp.domain.models.Movie
import com.example.shortfilmapp.domain.models.Trailer
import com.example.shortfilmapp.repository.MovieRepository
import kotlinx.coroutines.launch

class MovieViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _moviesState = MutableLiveData<MoviesState>()
    val moviesState: LiveData<MoviesState> = _moviesState

    fun loadPopularMovies() {
        viewModelScope.launch {
            _moviesState.value = MoviesState.Loading

            try {
                val movies = repository.getPopularMovies()
                if (movies.isNotEmpty()) {
                    _moviesState.value = MoviesState.Success(movies)
                } else {
                    _moviesState.value = MoviesState.Empty("No popular movies found")
                }
            } catch (e: Exception) {
                _moviesState.value = MoviesState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun loadTopRatedMovies() {
        viewModelScope.launch {
            _moviesState.value = MoviesState.Loading

            try {
                val movies = repository.getTopRatedMovies()
                if (movies.isNotEmpty()) {
                    _moviesState.value = MoviesState.Success(movies)
                } else {
                    _moviesState.value = MoviesState.Empty("No top rated movies found")
                }
            } catch (e: Exception) {
                _moviesState.value = MoviesState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun loadUpcomingMovies() {
        viewModelScope.launch {
            _moviesState.value = MoviesState.Loading

            try {
                val movies = repository.getUpcomingMovies()
                if (movies.isNotEmpty()) {
                    _moviesState.value = MoviesState.Success(movies)
                } else {
                    _moviesState.value = MoviesState.Empty("No upcoming movies found")
                }
            } catch (e: Exception) {
                _moviesState.value = MoviesState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            loadPopularMovies()
            return
        }

        viewModelScope.launch {
            _moviesState.value = MoviesState.Loading

            try {
                val movies = repository.searchMovies(query)
                if (movies.isNotEmpty()) {
                    _moviesState.value = MoviesState.Success(movies)
                } else {
                    _moviesState.value = MoviesState.Empty("No movies found for '$query'")
                }
            } catch (e: Exception) {
                _moviesState.value = MoviesState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun getMovieTrailers(movieId: Int, callback: (List<Trailer>) -> Unit) {
        viewModelScope.launch {
            try {
                val trailers = repository.getMovieTrailers(movieId)
                callback(trailers)
            } catch (e: Exception) {
                callback(emptyList())
            }
        }
    }

    sealed class MoviesState {
        object Loading : MoviesState()
        data class Success(val movies: List<Movie>) : MoviesState()
        data class Empty(val message: String) : MoviesState()
        data class Error(val message: String) : MoviesState()
    }
}