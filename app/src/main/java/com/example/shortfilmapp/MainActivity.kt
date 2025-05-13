package com.example.shortfilmapp

import android.util.Log
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shortfilmapp.api.RetrofitInstance
import com.example.shortfilmapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MovieAdapter
    private val apiKey = ("42ed2fc006c32959dcf686491a435ea7")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        RetrofitInstance.api.getPopularMovies(apiKey).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    Log.d("API_SUCCESS", "Received ${movies.size} movies") // Log the number of movies received
                    movies.forEach { movie ->
                        Log.d("MOVIE", "Title: ${movie.title}, Poster: ${movie.poster_path}")
                    }
                    adapter = MovieAdapter(movies)
                    binding.recyclerView.adapter = adapter
                } else {
                    Log.e("API_ERROR", "Response failed: ${response.errorBody()?.string()}")
                }
            }

            // Handle failure
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
