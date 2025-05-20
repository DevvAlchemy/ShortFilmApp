package com.example.shortfilmapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shortfilmapp.api.ApiClient
import com.example.shortfilmapp.databinding.ActivityMainBinding
import com.example.shortfilmapp.domain.models.Movie
import com.example.shortfilmapp.repository.MovieRepositoryImpl
import com.example.shortfilmapp.ui.adapters.MovieAdapter
import com.example.shortfilmapp.viewmodel.MovieViewModel
import com.example.shortfilmapp.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MovieViewModel
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Important: Initialize viewModel BEFORE using it in setupUI
        setupViewModel()
        setupUI()
        observeViewModel()
    }

    private fun setupViewModel() {
        val apiKey = getString(R.string.tmdb_api_key) // Make sure you have this in strings.xml
        val repository = MovieRepositoryImpl(ApiClient.movieApiService, apiKey)
        val factory = ViewModelFactory(repository, apiKey)

        viewModel = ViewModelProvider(this, factory)[MovieViewModel::class.java]
    }

    private fun setupUI() {
        // Set up toolbar
        binding.toolbar.title = getString(R.string.app_name)
        setSupportActionBar(binding.toolbar)

        // Initialize RecyclerView and adapter
        movieAdapter = MovieAdapter { movie ->
            navigateToPlayerActivity(movie)
        }

        binding.moviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = movieAdapter
            setHasFixedSize(true)
        }

        // Setup refresh layout if it exists
        if (::binding.isInitialized && binding::class.java.getDeclaredField("swipeRefreshLayout") != null) {
            binding.swipeRefreshLayout.setOnRefreshListener {
                viewModel.loadPopularMovies()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = getString(R.string.search_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    viewModel.searchMovies(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                viewModel.loadPopularMovies()
                true
            }

            R.id.action_popular -> {
                binding.toolbar.title = getString(R.string.popular)
                viewModel.loadPopularMovies()
                true
            }

            R.id.action_top_rated -> {
                binding.toolbar.title = getString(R.string.top_rated)
                viewModel.loadTopRatedMovies()
                true
            }

            R.id.action_upcoming -> {
                binding.toolbar.title = getString(R.string.upcoming)
                viewModel.loadUpcomingMovies()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeViewModel() {
        viewModel.moviesState.observe(this) { state ->
            // Handle swipe refresh if available
            if (::binding.isInitialized &&
                binding::class.java.getDeclaredField("swipeRefreshLayout") != null &&
                binding.swipeRefreshLayout.isRefreshing
            ) {
                binding.swipeRefreshLayout.isRefreshing = false
            }

            when (state) {
                is MovieViewModel.MoviesState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.moviesRecyclerView.visibility = View.GONE
                    binding.errorMessage.visibility = View.GONE
                }

                is MovieViewModel.MoviesState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.moviesRecyclerView.visibility = View.VISIBLE
                    binding.errorMessage.visibility = View.GONE

                    movieAdapter.submitList(state.movies)
                }

                is MovieViewModel.MoviesState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.moviesRecyclerView.visibility = View.GONE
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.errorMessage.text = state.message

                    showRetrySnackbar(state.message)
                }

                is MovieViewModel.MoviesState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.moviesRecyclerView.visibility = View.GONE
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.errorMessage.text = state.message
                }
            }
        }

        // Load movies when the activity starts
        viewModel.loadPopularMovies()
    }

    private fun showRetrySnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.retry)) {
                viewModel.loadPopularMovies()
            }
            .show()
    }

    private fun navigateToPlayerActivity(movie: Movie) {
        try {
            // Start PlayerActivity with a hard-coded trailer ID for testing
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("MOVIE_TITLE", movie.title)
                // Use a known working YouTube video ID
                putExtra("VIDEO_ID", "dQw4w9WgXcQ")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error opening trailer", Toast.LENGTH_SHORT).show()
        }
    }
}
