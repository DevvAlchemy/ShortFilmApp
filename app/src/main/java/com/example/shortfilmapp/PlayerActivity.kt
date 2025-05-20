package com.example.shortfilmapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.shortfilmapp.api.ApiClient
import com.example.shortfilmapp.databinding.ActivityPlayerBinding
import com.example.shortfilmapp.repository.MovieRepositoryImpl
import com.example.shortfilmapp.viewmodel.MovieViewModel
import com.example.shortfilmapp.viewmodel.ViewModelFactory
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: MovieViewModel
    private var player: YouTubePlayer? = null
    private lateinit var youTubePlayerView: YouTubePlayerView

    companion object {
        const val EXTRA_MOVIE_ID = "extra_movie_id"
        const val EXTRA_VIDEO_ID = "extra_video_id"
        const val EXTRA_VIDEO_TITLE = "extra_video_title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from intent
        val movieId = intent.getIntExtra(EXTRA_MOVIE_ID, -1)
        val videoId = intent.getStringExtra(EXTRA_VIDEO_ID)
        val videoTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: "Movie Trailer"

        // Find views directly instead of using binding
        // This approach will work regardless of your actual layout structure
        youTubePlayerView = findViewById(R.id.youtubePlayerView)

        // Set up player
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youtubePlayer: YouTubePlayer) {
                player = youtubePlayer

                if (videoId != null && videoId.isNotEmpty()) {
                    // If we already have a video ID, play it
                    youtubePlayer.loadVideo(videoId, 0f)
                } else if (movieId != -1) {
                    // Otherwise, fetch trailers for the movie
                    setupViewModel()
                    loadTrailers(movieId)
                } else {
                    // No way to get a trailer
                    Toast.makeText(this@PlayerActivity, "No trailer information available", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        })

        // Set the title in a simple way
        title = videoTitle
    }

    private fun setupViewModel() {
        val apiKey = getString(R.string.tmdb_api_key)
        val repository = MovieRepositoryImpl(ApiClient.movieApiService, apiKey)
        val factory = ViewModelFactory(repository, apiKey)
        viewModel = ViewModelProvider(this, factory)[MovieViewModel::class.java]
    }

    private fun loadTrailers(movieId: Int) {
        // Show loading indicator if you have one
        val progressBar = findViewById<View>(R.id.progressBar)
        progressBar?.visibility = View.VISIBLE

        viewModel.getMovieTrailers(movieId) { trailers ->
            // Hide loading indicator
            progressBar?.visibility = View.GONE

            if (trailers.isNotEmpty()) {
                val trailer = trailers.first()
                player?.loadVideo(trailer.key, 0f)
            } else {
                Toast.makeText(this, "No trailers available for this movie", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player = null
    }
}