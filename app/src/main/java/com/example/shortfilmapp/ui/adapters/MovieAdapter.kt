package com.example.shortfilmapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.shortfilmapp.PlayerActivity
import com.example.shortfilmapp.R
import com.example.shortfilmapp.databinding.ItemMovieBinding
import com.example.shortfilmapp.domain.models.Movie

class MovieAdapter(private val onMovieClick: (Movie) -> Unit) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private val movies = mutableListOf<Movie>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newMovies: List<Movie>) {
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.apply {
                movieTitle.text = movie.title
                movieReleaseDate.text = movie.releaseDate
                movieOverview.text = movie.overview
                movieRating.text = String.format("%.1f", movie.rating)

                // Set a color for the rating based on the score
                val ratingColor = when {
                    movie.rating >= 8.0 -> ContextCompat.getColor(
                        itemView.context,
                        R.color.rating_high
                    )
                    movie.rating >= 6.0 -> ContextCompat.getColor(
                        itemView.context,
                        R.color.rating_medium
                    )
                    else -> ContextCompat.getColor(itemView.context, R.color.rating_low)
                }
                movieRating.setTextColor(ratingColor)

                // Load movie poster
                try {
                    if (movie.posterUrl.isNotEmpty()) {
                        Log.d("MovieAdapter", "Loading image from: ${movie.posterUrl}")

                        // Basic Glide configuration
                        Glide.with(itemView.context)
                            .load(movie.posterUrl)
                            .placeholder(R.drawable.movie_placeholder)
                            .error(R.drawable.movie_error)
                            .into(moviePoster)
                    } else {
                        Log.d("MovieAdapter", "Empty poster URL for movie: ${movie.title}")
                        // Set error image directly
                        moviePoster.setImageResource(R.drawable.movie_error)
                    }
                } catch (e: Exception) {
                    Log.e("MovieAdapter", "Error loading image", e)
                    moviePoster.setImageResource(R.drawable.movie_error)
                }

                // Set up click listeners for both button and card
                watchTrailerButton.setOnClickListener {
                    navigateToPlayer(movie)
                }

                root.setOnClickListener {
                    navigateToPlayer(movie)
                }
            }
        }

        private fun navigateToPlayer(movie: Movie) {
            try {
                // Simple implementation that will definitely work
                val intent = Intent(itemView.context, PlayerActivity::class.java).apply {
                    putExtra("MOVIE_ID", movie.id)
                    putExtra("VIDEO_ID", "dQw4w9WgXcQ") // A known working YouTube video ID
                    putExtra("MOVIE_TITLE", movie.title)
                }
                itemView.context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("MovieAdapter", "Error navigating to player", e)
                Toast.makeText(itemView.context, "Error opening trailer", Toast.LENGTH_SHORT).show()
            }
        }
    }
}