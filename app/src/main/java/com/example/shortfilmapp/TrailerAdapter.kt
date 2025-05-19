package com.example.shortfilmapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shortfilmapp.databinding.ItemTrailerBinding
import com.example.shortfilmapp.domain.models.Trailer

//import com.example.shortfilmapp.ui.adapters.TrailerAdapter

// ui/adapters/TrailerAdapter.kt
class TrailerAdapter(private val onTrailerClick: (Trailer) -> Unit) :
    RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>() {

    private val trailers = mutableListOf<Trailer>()

    fun submitList(newTrailers: List<Trailer>) {
        trailers.clear()
        trailers.addAll(newTrailers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
        val binding = ItemTrailerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TrailerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
        holder.bind(trailers[position])
    }

    override fun getItemCount() = trailers.size

    inner class TrailerViewHolder(private val binding: ItemTrailerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trailer: Trailer) {
            binding.apply {
                textViewTrailerName.text = trailer.name
                imageViewTrailerType.setImageResource(
                    when (trailer.type.lowercase()) {
                        "trailer" -> R.drawable.ic_play
                        "teaser" -> R.drawable.ic_teaser
                        "featurette" -> R.drawable.ic_featurette
                        "clip" -> R.drawable.ic_clip
                        else -> R.drawable.ic_play
                    }
                )

                // Set click listener
                root.setOnClickListener {
                    onTrailerClick(trailer)
                }
            }
        }
    }
}