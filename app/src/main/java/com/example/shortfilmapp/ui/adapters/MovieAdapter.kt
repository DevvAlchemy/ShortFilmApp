package com.example.shortfilmapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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
                textViewTitle.text = movie.title
                textViewReleaseDate.text = movie.releaseDate
                textViewOverview.text = movie.overview

                // Set rating (convert from 10 to 5 star scale)
                val ratingOutOf5 = (movie.rating / 2).toFloat()
                ratingBar.rating = ratingOutOf5
                textViewRating.text = String.format("%.1f/10", movie.rating)

                // Color code the rating text
                textViewRating.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        when {
                            movie.rating >= 7.0 -> R.color.rating_good
                            movie.rating >= 5.0 -> R.color.rating_average
                            else -> R.color.rating_bad
                        }
                    )
                )

                // Load poster image
                Glide.with(itemView.context)
                    .load(movie.posterUrl)
                    .placeholder(R.drawable.placeholder_poster)
                    .error(R.drawable.error_poster)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageViewPoster)

                // Set click listener
                buttonWatchTrailer.setOnClickListener {
                    onMovieClick(movie)
                }

                // Make the entire card clickable
                root.setOnClickListener {
                    onMovieClick(movie)
                }
            }
        }
    }
}