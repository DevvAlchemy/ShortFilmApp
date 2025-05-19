package com.example.shortfilmapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shortfilmapp.viewmodel.MovieViewModel
import com.example.shortfilmapp.TrailerPlayerViewModel
import com.example.shortfilmapp.repository.MovieRepository

class ViewModelFactory(
    private val repository: MovieRepository,
    private val apiKey: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(TrailerPlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrailerPlayerViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}