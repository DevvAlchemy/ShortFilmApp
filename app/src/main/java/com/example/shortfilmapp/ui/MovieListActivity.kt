package com.example.shortfilmapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shortfilmapp.domain.models.Movie
import com.example.shortfilmapp.PlayerActivity
import com.example.shortfilmapp.R
import com.example.shortfilmapp.viewmodel.ViewModelFactory
import com.example.shortfilmapp.ui.adapters.MovieAdapter
import com.example.shortfilmapp.api.ApiClient
import com.example.shortfilmapp.databinding.ActivityMovieListBinding
import com.example.shortfilmapp.repository.MovieRepositoryImpl
import com.example.shortfilmapp.viewmodel.MovieViewModel
import com.google.android.material.snackbar.Snackbar

class MovieListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieListBinding
    private lateinit var viewModel: MovieViewModel
    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupViewModel()
        observeViewModel()
    }

    private fun setupUI() {
        // Set up toolbar
        binding.toolbar.title = getString(R.string.popular_movies)
        setSupportActionBar(binding.toolbar)

        // Set up RecyclerView and adapter
        adapter = MovieAdapter { movie ->
            navigateToPlayerActivity(movie)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMovies.layoutManager = layoutManager
        binding.recyclerViewMovies.adapter = adapter
        binding.recyclerViewMovies.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
        binding.recyclerViewMovies.setHasFixedSize(true)
    }

    private fun setupViewModel() {
        val apiKey = getString(R.string.tmdb_api_key)
        val repository = MovieRepositoryImpl(ApiClient.movieApiService, apiKey)
        val factory = ViewModelFactory(repository, apiKey)

        viewModel = ViewModelProvider(this, factory)[MovieViewModel::class.java]
        viewModel.loadPopularMovies()
    }

    private fun observeViewModel() {
        viewModel.moviesState.observe(this) { state ->
            when (state) {
                is MovieViewModel.MoviesState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerViewMovies.visibility = View.GONE
                    binding.textViewError.visibility = View.GONE
                }
                is MovieViewModel.MoviesState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewMovies.visibility = View.VISIBLE
                    binding.textViewError.visibility = View.GONE

                    adapter.submitList(state.movies)
                }
                is MovieViewModel.MoviesState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewMovies.visibility = View.GONE
                    binding.textViewError.visibility = View.VISIBLE

                    binding.textViewError.text = state.message
                    showRetrySnackbar(state.message)
                }
                else -> {
                    // Handle other states if needed
                }
            }
        }
    }

    private fun showRetrySnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.retry)) {
                viewModel.loadPopularMovies()
            }
            .show()
    }

    private fun navigateToPlayerActivity(movie: Movie) {
        viewModel.getMovieTrailers(movie.id) { trailers ->
            if (trailers.isNotEmpty()) {
                val trailer = trailers.first()
                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra(PlayerActivity.EXTRA_VIDEO_ID, trailer.key)
                    putExtra(PlayerActivity.EXTRA_VIDEO_TITLE, movie.title)
                }
                startActivity(intent)
            } else {
                Snackbar.make(
                    binding.root,
                    "No trailers found for this movie",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }
}