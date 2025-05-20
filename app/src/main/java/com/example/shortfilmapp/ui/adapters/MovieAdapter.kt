package com.example.shortfilmapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.target.Target
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
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

    // In MovieAdapter.kt
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

                // In your MovieAdapter.kt ViewHolder bind method
                // Use a simpler Glide configuration
                try {
                    if (movie.posterUrl.isNotEmpty()) {
                        Log.d("MovieAdapter", "Loading image from: ${movie.posterUrl}")

                        // Basic Glide configuration
                        Glide.with(itemView.context)
                            .load(movie.posterUrl)
                            .placeholder(R.drawable.movie_placeholder)
                            .error(R.drawable.movie_error)
                            .into(binding.moviePoster)
                    } else {
                        Log.d("MovieAdapter", "Empty poster URL for movie: ${movie.title}")
                        // Set error image directly
                        binding.moviePoster.setImageResource(R.drawable.movie_error)
                    }
                } catch (e: Exception) {
                    Log.e("MovieAdapter", "Error loading image", e)
                    binding.moviePoster.setImageResource(R.drawable.movie_error)
                }
//                    .into(binding.moviePoster)
//                // Set up click listeners
                watchTrailerButton.setOnClickListener {
                    onMovieClick(movie)
                }

                // In your MovieAdapter.kt ViewHolder bind method
                // Try loading a test image first to see if Glide is configured correctly
                val testImageUrl = "https://image.tmdb.org/t/p/w500/8Vt6mWEReuy4Of61Lnj5Xj704m8.jpg" // Spider-Man: No Way Home poster
                Log.d("MovieAdapter", "Loading test image from: $testImageUrl")

                Glide.with(itemView.context)
                    .load(testImageUrl)
                    .placeholder(R.drawable.movie_placeholder)
                    .error(R.drawable.movie_error)
                    .into(binding.moviePoster)

                // In your MovieAdapter.kt
                // In your MovieAdapter.kt
                root.setOnClickListener {
                    try {
                        // Use a simple Intent to open PlayerActivity without trying to load trailers
                        val intent = Intent(itemView.context, PlayerActivity::class.java).apply {
                            putExtra(PlayerActivity.EXTRA_MOVIE_ID, movie.id)
                            // Pass a placeholder video ID for testing
                            putExtra(PlayerActivity.EXTRA_VIDEO_ID, "dQw4w9WgXcQ") // A known working YouTube video ID
                            putExtra(PlayerActivity.EXTRA_VIDEO_TITLE, movie.title)
                        }
                        itemView.context.startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("MovieAdapter", "Error navigating to player", e)
                        Toast.makeText(itemView.context, "Error opening trailer", Toast.LENGTH_SHORT).show()
                    }
                }
                }
            }
        }

    }
                // Make the entire card clickable
//                root.setOnClickListener {
//                    onMovieClick(movie)
//                }
//            }
//        }
//    }
//}