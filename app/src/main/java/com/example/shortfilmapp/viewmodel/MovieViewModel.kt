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

    private val _trailers = MutableLiveData<List<Trailer>>()
    val trailers: LiveData<List<Trailer>> = _trailers

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _apiKey = MutableLiveData<String>()

    fun setApiKey(apiKey: String) {
        _apiKey.value = apiKey
        loadPopularMovies()
    }

    fun loadPopularMovies() {
        viewModelScope.launch {
            _moviesState.value = MoviesState.Loading

            try {
                val movies = repository.getPopularMovies()
                if (movies.isNotEmpty()) {
                    _moviesState.value = MoviesState.Success(movies)
                } else {
                    _moviesState.value = MoviesState.Error("No movies found")
                }
            } catch (e: Exception) {
                _moviesState.value = MoviesState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun loadMovieTrailers(movieId: Int) {
        viewModelScope.launch {
            try {
                val trailerList = repository.getMovieTrailers(movieId)
                _trailers.value = trailerList
                if (trailerList.isEmpty()) {
                    _error.value = "No trailers found for this movie"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
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