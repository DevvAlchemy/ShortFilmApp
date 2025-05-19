package com.example.shortfilmapp

import android.R.attr.apiKey
import android.app.Application
import android.content.Context
import com.example.shortfilmapp.api.ApiClient
import com.example.shortfilmapp.repository.MovieRepository
import com.example.shortfilmapp.repository.MovieRepositoryImpl

class MovieApp : Application() {

    lateinit var movieRepository: MovieRepository
    lateinit var networkStatusObserver: NetworkStatusObserver

    override fun onCreate() {
        super.onCreate()

        val apiKey = getString(R.string.tmdb_api_key)

        movieRepository = MovieRepositoryImpl(ApiClient.movieApiService, apiKey)
        networkStatusObserver = NetworkStatusObserver(this)
    }

    companion object {
        fun from(context: Context): MovieApp {
            return context.applicationContext as MovieApp
        }
    }
}