package com.example.shortfilmapp.ui

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shortfilmapp.PlayerActivity
import com.example.shortfilmapp.R
import com.example.shortfilmapp.api.ApiClient
import com.example.shortfilmapp.databinding.ActivityMovieListBinding
import com.example.shortfilmapp.domain.models.Movie
import com.example.shortfilmapp.repository.MovieRepositoryImpl
import com.example.shortfilmapp.ui.adapters.MovieAdapter
import com.example.shortfilmapp.viewmodel.MovieViewModel
import com.example.shortfilmapp.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class MovieListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieListBinding
    private lateinit var viewModel: MovieViewModel
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up ViewModel first
        setupViewModel()
        // Then set up UI
        setupUI()
        // Finally observe ViewModel
        observeViewModel()
    }

    private fun setupViewModel() {
        val apiKey = getString(R.string.tmdb_api_key)
        val repository = MovieRepositoryImpl(ApiClient.movieApiService, apiKey)
        val factory = ViewModelFactory(repository, apiKey)

        viewModel = ViewModelProvider(this, factory)[MovieViewModel::class.java]
    }

    private fun setupUI() {
        // Set up toolbar
        binding.toolbar.title = getString(R.string.popular_movies)
        setSupportActionBar(binding.toolbar)

        // Set up RecyclerView and adapter
        movieAdapter = MovieAdapter { movie ->
            navigateToTrailerPlayer(movie)
        }

        binding.recyclerViewMovies.apply {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(this@MovieListActivity)
            addItemDecoration(
                DividerItemDecoration(
                    this@MovieListActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
            setHasFixedSize(true)
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

                    movieAdapter.submitList(state.movies)
                }
                is MovieViewModel.MoviesState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewMovies.visibility = View.GONE
                    binding.textViewError.visibility = View.VISIBLE

                    binding.textViewError.text = state.message
                    showRetrySnackbar(state.message)
                }
                is MovieViewModel.MoviesState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewMovies.visibility = View.GONE
                    binding.textViewError.visibility = View.VISIBLE

                    binding.textViewError.text = state.message
                }
            }
        }

        // Load popular movies when starting
        viewModel.loadPopularMovies()
    }

    private fun showRetrySnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.retry)) {
                viewModel.loadPopularMovies()
            }
            .show()
    }

    private fun navigateToTrailerPlayer(movie: Movie) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("MOVIE_ID", movie.id)
            putExtra("VIDEO_ID", "dQw4w9WgXcQ") // Known working ID for testing
            putExtra("MOVIE_TITLE", movie.title)
        }
        startActivity(intent)
    }
}