package com.example.shortfilmapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class PlayerActivity : AppCompatActivity() {

    private var youTubePlayer: YouTubePlayer? = null
    private lateinit var youTubePlayerView: YouTubePlayerView

    companion object {
        const val EXTRA_MOVIE_ID = "extra_movie_id"
        const val EXTRA_VIDEO_ID = "extra_video_id"
        const val EXTRA_VIDEO_TITLE = "extra_video_title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        try {
            // Get data from intent with safe defaults
            val videoId = intent.getStringExtra(EXTRA_VIDEO_ID) ?: "dQw4w9WgXcQ"
            val videoTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: "Movie Trailer"

            // Set the title of the activity
            title = videoTitle

            // Initialize the YouTube player
            youTubePlayerView = findViewById(R.id.youtubePlayerView)
            lifecycle.addObserver(youTubePlayerView)

            // Set up the player
            youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(player: YouTubePlayer) {
                    youTubePlayer = player

                    try {
                        // Load the video
                        Log.d("PlayerActivity", "Loading video ID: $videoId")
                        player.loadVideo(videoId, 0f)
                    } catch (e: Exception) {
                        Log.e("PlayerActivity", "Error loading video", e)
                        Toast.makeText(this@PlayerActivity, "Error playing video", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("PlayerActivity", "Error in onCreate", e)
            Toast.makeText(this, "Error initializing player", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayer = null
    }
}