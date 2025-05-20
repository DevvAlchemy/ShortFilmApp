package com.example.shortfilmapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class PlayerActivity : AppCompatActivity() {

    private lateinit var youTubePlayerView: YouTubePlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        try {
            // Get data from intent
            val videoId = intent.getStringExtra("VIDEO_ID") ?: "dQw4w9WgXcQ"
            val movieTitle = intent.getStringExtra("MOVIE_TITLE") ?: "Movie Trailer"

            Log.d("PlayerActivity", "Video ID: $videoId, Title: $movieTitle")

            // Set the title
            title = movieTitle

            // Initialize the YouTube player
            youTubePlayerView = findViewById(R.id.youtubePlayerView)
            lifecycle.addObserver(youTubePlayerView)

            youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(player: YouTubePlayer) {
                    Log.d("PlayerActivity", "YouTube player ready, loading video: $videoId")
                    // Load the video once the player is ready
                    player.loadVideo(videoId, 0f)
                }
            })
        } catch (e: Exception) {
            Log.e("PlayerActivity", "Error in PlayerActivity", e)
            Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::youTubePlayerView.isInitialized) {
            youTubePlayerView.release()
        }
    }
}