package com.example.shortfilmapp
//

//import android.os.Bundle
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.ViewModelProvider
//import com.example.shortfilmapp.api.ApiClient
//import com.example.shortfilmapp.repository.MovieRepositoryImpl
//import com.example.shortfilmapp.viewmodel.MovieViewModel
//import com.example.shortfilmapp.viewmodel.ViewModelFactory
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
//
//
//class TrailerActivity : AppCompatActivity() {
//
//    private lateinit var viewModel: MovieViewModel
//    private lateinit var youTubePlayerView: YouTubePlayerView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_trailer_player)
//
//        val movieId = intent.getIntExtra("MOVIE_ID", -1)
//        val movieTitle = intent.getStringExtra("MOVIE_TITLE") ?: "Movie Trailer"
//
//        findViewById<TextView>(R.id.textViewTrailerName).text = movieTitle
//
//        youTubePlayerView = findViewById(R.id.youtubePlayerView)
//        lifecycle.addObserver(youTubePlayerView)
//
//        setupViewModel()
//        observeViewModel()
//
//        if (movieId != -1) {
//            viewModel.loadMovieTrailers(movieId)
//        } else {
//            finish()
//        }
//    }
//
//    private fun setupViewModel() {
//        val apiKey = getString(R.string.tmdb_api_key)
//        val repository = MovieRepositoryImpl(ApiClient.movieApiService, apiKey)
//        val factory = ViewModelFactory(repository, apiKey)
//        viewModel = ViewModelProvider(this, factory)[MovieViewModel::class.java]
//    }
//
//    private fun observeViewModel() {
//        viewModel.trailers.observe(this) { trailers ->
//            if (trailers.isNotEmpty()) {
//                // Get the first trailer (usually the official trailer)
//                val trailer = trailers.firstOrNull {
//                    it.type.equals("Trailer", ignoreCase = true)
//                } ?: trailers.first()
//
//                youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//                    override fun onReady(youTubePlayer: YouTubePlayer) {
//                        youTubePlayer.loadVideo(trailer.key, 0f)
//                    }
//                })
//            } else {
//                Toast.makeText(this, "No trailers found for this movie", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        }
//
//        viewModel.error.observe(this) { errorMessage ->
//            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
//            finish()
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        youTubePlayerView.release()
//    }
//}


import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class TrailerActivity : AppCompatActivity() {

    private lateinit var youTubePlayerView: YouTubePlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trailer_player)

        val movieId = intent.getIntExtra("MOVIE_ID", -1)
        val movieTitle = intent.getStringExtra("MOVIE_TITLE") ?: "Movie Trailer"

        findViewById<TextView>(R.id.trailerTitle).text = movieTitle

        youTubePlayerView = findViewById(R.id.youtubePlayerView)
        lifecycle.addObserver(youTubePlayerView)

        // Use a dummy video ID (for now)
        val dummyVideoId = "dQw4w9WgXcQ" // Rick Roll

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(dummyVideoId, 0f)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayerView.release()
    }
}