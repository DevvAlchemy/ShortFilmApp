package com.example.shortfilmapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shortfilmapp.databinding.ActivityPlayerBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import android.view.View

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var youTubePlayer: YouTubePlayer? = null

    companion object {
        const val EXTRA_VIDEO_ID = "extra_video_id"
        const val EXTRA_VIDEO_TITLE = "extra_video_title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get video ID and title from intent
        val videoId = intent.getStringExtra(EXTRA_VIDEO_ID) ?: ""
        val videoTitle = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: "Movie Trailer"

        // Set the video title
        binding.videoTitle.text = videoTitle

        // Set up YouTube Player
        lifecycle.addObserver(binding.videoView)

        binding.videoView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(player: YouTubePlayer) {
                youTubePlayer = player

                // Show loading indicator
                binding.progressBar.visibility = View.VISIBLE

                if (videoId.isNotEmpty()) {
                    player.loadVideo(videoId, 0f)
                } else {
                    // Handle error - no video ID
                    showError("Invalid video ID")
                }
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
            ) {
                if (state == com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState.PLAYING) {
                    // Hide loading indicator once video starts playing
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onError(
                youTubePlayer: YouTubePlayer,
                error: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerError
            ) {
                showError("Error playing video: ${error.name}")
            }
        })
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.videoTitle.text = message
    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayer = null
    }
}