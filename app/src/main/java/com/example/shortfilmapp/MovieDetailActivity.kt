package com.example.shortfilmapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.shortfilmapp.databinding.ActivityMovieDetailBinding
import com.example.shortfilmapp.domain.models.Movie
import com.google.android.material.snackbar.Snackbar

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailBinding

    companion object {
        const val EXTRA_MOVIE = "extra_movie"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movie = intent.getParcelableExtra<Movie>(EXTRA_MOVIE)
        if (movie == null) {
            Snackbar.make(binding.root, "Movie details not available", Snackbar.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI(movie)
        setupToolbar()
    }

    private fun setupUI(movie: Movie) {
        binding.textViewTitle.text = movie.title
        binding.textViewOverview.text = movie.overview
        binding.textViewReleaseDate.text = "Released: ${movie.releaseDate}"
        binding.textViewRating.text = "Rating: ${movie.rating}/10"

        // Load backdrop image
        Glide.with(this)
            .load(movie.backdropUrl)
            .placeholder(R.drawable.placeholder_backdrop)
            .error(R.drawable.error_backdrop)
            .into(binding.imageViewBackdrop)

        // Load poster image
        Glide.with(this)
            .load(movie.posterUrl)
            .placeholder(R.drawable.placeholder_poster)
            .error(R.drawable.error_poster)
            .into(binding.imageViewPoster)

        binding.buttonWatchTrailer.setOnClickListener {
            navigateToTrailerPlayer(movie)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun navigateToTrailerPlayer(movie: Movie) {
        // Your existing code to navigate to the trailer player
    }
}