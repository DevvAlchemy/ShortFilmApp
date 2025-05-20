package com.example.shortfilmapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.shortfilmapp.MovieDetailActivity
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

                // Load poster image
                Glide.with(itemView.context)
                    .load(movie.posterUrl)
                    .placeholder(R.drawable.placeholder_poster)
                    .error(R.drawable.error_poster)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(moviePoster)

                // Set up click listeners
                watchTrailerButton.setOnClickListener {
                    onMovieClick(movie)
                }

                root.setOnClickListener {
                    // Navigate to details screen
                    val intent = Intent(itemView.context, MovieDetailActivity::class.java).apply {
                        putExtra(MovieDetailActivity.EXTRA_MOVIE, movie)
                    }
                    itemView.context.startActivity(intent)
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