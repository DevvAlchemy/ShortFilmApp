//package com.example.shortfilmapp
//
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.ViewModelProvider
//import com.example.shortfilmapp.api.ApiClient
//import com.example.shortfilmapp.databinding.ActivityTrailerPlayerBinding
//import com.example.shortfilmapp.repository.MovieRepositoryImpl
//import com.example.shortfilmapp.viewmodel.MovieViewModel
//import com.example.shortfilmapp.viewmodel.ViewModelFactory
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
//
//class TrailerPlayerActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityTrailerPlayerBinding
//    private lateinit var viewModel: MovieViewModel
//    private var youTubePlayer: YouTubePlayer? = null
//
//    companion object {
//        const val EXTRA_MOVIE_ID = "extra_movie_id"
//        const val EXTRA_MOVIE_TITLE = "extra_movie_title"
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityTrailerPlayerBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Get movie info from intent
//        val movieId = intent.getIntExtra(EXTRA_MOVIE_ID, -1)
//        val movieTitle = intent.getStringExtra(EXTRA_MOVIE_TITLE) ?: "Movie Trailer"
//
//        // Set up toolbar
//        binding.toolbar.title = movieTitle
//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
//
//        // Set up movie title
//        binding.textViewMovieTitle.text = movieTitle
//
//        setupViewModel()
//
//        // Set up YouTube player
//        lifecycle.addObserver(binding.youtubePlayerView)
//        binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//            override fun onReady(player: YouTubePlayer) {
//                youTubePlayer = player
//            }
//        })
//
//        // Load trailers
//        if (movieId != -1) {
//            viewModel.loadMovieTrailers(movieId)
//            observeViewModel()
//        } else {
//            Toast.makeText(this, "Invalid movie ID", Toast.LENGTH_SHORT).show()
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
//                binding.textViewTrailerName.text = trailer.name
//                youTubePlayer?.loadVideo(trailer.key, 0f)
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
//        youTubePlayer = null
//    }
//}

package com.example.shortfilmapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shortfilmapp.databinding.ActivityTrailerPlayerBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

class TrailerPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrailerPlayerBinding
    private var youTubePlayer: YouTubePlayer? = null

    companion object {
        const val EXTRA_VIDEO_ID = "extra_video_id"
        const val EXTRA_VIDEO_TITLE = "extra_video_title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrailerPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from intent
        val videoId = intent.getStringExtra(EXTRA_VIDEO_ID) ?: ""
        val videoTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: "Movie Trailer"

        if (videoId.isEmpty()) {
            Toast.makeText(this, "Video ID not provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set up toolbar
        binding.toolbar.title = videoTitle
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        // Set up YouTube player
        binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(player: YouTubePlayer) {
                youTubePlayer = player
                player.loadVideo(videoId, 0f)
            }
        })
        lifecycle.addObserver(binding.youtubePlayerView)

        // Display the video title
        binding.textViewVideoTitle.text = videoTitle
    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayer = null
    }
}


