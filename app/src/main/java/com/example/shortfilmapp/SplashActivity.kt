package com.example.shortfilmapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shortfilmapp.databinding.ActivitySplashBinding
import com.example.shortfilmapp.ui.MovieListActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Simple animation
        binding.logoImageView.alpha = 0f
        binding.logoImageView.animate()
            .alpha(1f)
            .setDuration(1500)
            .withEndAction {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

                // Add a fade-in animation when starting the main activity
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            .start()
    }
}