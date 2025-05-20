package com.example.shortfilmapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shortfilmapp.api.ApiClient
import com.example.shortfilmapp.databinding.ActivityMainBinding
import com.example.shortfilmapp.domain.models.Movie
import com.example.shortfilmapp.repository.MovieRepositoryImpl
import com.example.shortfilmapp.ui.adapters.MovieAdapter
import com.example.shortfilmapp.viewmodel.MovieViewModel
import com.example.shortfilmapp.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MovieViewModel
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Set up toolbar
        binding.toolbar.title = getString(R.string.app_name)
        setSupportActionBar(binding.toolbar)

        // Initialize RecyclerView and adapter
        movieAdapter = MovieAdapter { movie ->
            navigateToTrailerPlayer(movie)
        }

        binding.moviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = movieAdapter
            setHasFixedSize(true)
        }

        // pull to refresh functionality
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadPopularMovies()
        }
        viewModel.moviesState.observe (this) { state ->
            binding.swipeRefreshLayout.isRefreshing = state is MovieViewModel.MoviesState.Loading
        }
    }

    private fun setupViewModel() {
        val apiKey = getString(R.string.tmdb_api_key) // Make sure you have this in strings.xml
        val repository = MovieRepositoryImpl(ApiClient.movieApiService, apiKey)
        val factory = ViewModelFactory(repository, apiKey)

        viewModel = ViewModelProvider(this, factory)[MovieViewModel::class.java]
    }

    private fun observeViewModel() {
        viewModel.moviesState.observe(this) { state ->
            // Stop refreshing animation if active
            if (binding.swipeRefreshLayout.isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = false
            }

            when (state) {
                is MovieViewModel.MoviesState.Loading -> showLoading()
                is MovieViewModel.MoviesState.Success -> showMovies(state.movies)
                is MovieViewModel.MoviesState.Error -> showError(state.message)
                is MovieViewModel.MoviesState.Empty -> showEmpty(state.message)
            }
        }

        // Load movies when the activity starts
        viewModel.loadPopularMovies()
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.moviesRecyclerView.visibility = View.GONE
        binding.errorMessage.visibility = View.GONE
    }

    private fun showMovies(movies: List<Movie>) {
        binding.progressBar.visibility = View.GONE
        binding.moviesRecyclerView.visibility = View.VISIBLE
        binding.errorMessage.visibility = View.GONE

        movieAdapter.submitList(movies)
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.moviesRecyclerView.visibility = View.GONE
        binding.errorMessage.visibility = View.VISIBLE
        binding.errorMessage.text = message

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showEmpty(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.moviesRecyclerView.visibility = View.GONE
        binding.errorMessage.visibility = View.VISIBLE
        binding.errorMessage.text = message
    }

    private fun navigateToTrailerPlayer(movie: Movie) {
        viewModel.getMovieTrailers(movie.id) { trailers ->
            if (trailers.isNotEmpty()) {
                val trailer = trailers.first()
                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra(PlayerActivity.EXTRA_VIDEO_ID, trailer.key)
                    putExtra(PlayerActivity.EXTRA_VIDEO_TITLE, movie.title)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "No trailers found for this movie", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
