package com.example.shortfilmapp

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.shortfilmapp.databinding.ActivityPlayerBinding
import androidx.core.net.toUri

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the Movie object passed from MainActivity
        val movie = intent.getSerializableExtra("movie") as? Movie

        movie?.let {
            binding.videoTitle.text = it.title

            // NOTE: Replace this with an actual playable video URL
            val videoUri: Uri =
                "https://www.sample-videos.com/video123/mp4/720/big_buck_bunny_720p_10mb.mp4".toUri()

            binding.videoView.setVideoURI(videoUri)

            val mediaController = MediaController(this)
            mediaController.setAnchorView(binding.videoView)
            binding.videoView.setMediaController(mediaController)

            binding.videoView.start()
        }
    }
}