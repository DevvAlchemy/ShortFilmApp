package com.example.shortfilmapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shortfilmapp.domain.models.Trailer
import com.example.shortfilmapp.repository.MovieRepository
import kotlinx.coroutines.launch

class  TrailerPlayerViewModel(
private val repository: MovieRepository
) : ViewModel() {

    private val _trailersState = MutableLiveData<TrailersState>(TrailersState.Loading)
    val trailersState: LiveData<TrailersState> = _trailersState

    private val _apiKey = MutableLiveData<String>()

    fun setApiKey(apiKey: String) {
        _apiKey.value = apiKey
    }

    fun loadMovieTrailers(movieId: Int) {
        viewModelScope.launch {
            _trailersState.value = TrailersState.Loading

            try {
                val trailers = repository.getMovieTrailers(movieId)
                if (trailers.isNotEmpty()) {
                    _trailersState.value = TrailersState.Success(trailers)
                } else {
                    _trailersState.value = TrailersState.Error("No trailers found")
                }
            } catch (e: Exception) {
                _trailersState.value = TrailersState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    sealed class TrailersState {
        object Loading : TrailersState()
        data class Success(val trailers: List<Trailer>) : TrailersState()
        data class Error(val message: String) : TrailersState()
    }
}