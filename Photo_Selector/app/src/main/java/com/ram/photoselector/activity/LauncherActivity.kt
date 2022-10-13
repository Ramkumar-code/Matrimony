package com.ram.photoselector.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ram.photoselector.databinding.ActivitySplashBinding


class LauncherActivity:AppCompatActivity() {
    private val SPLASH_TIME_OUT = 3000
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        Handler(Looper.getMainLooper()).postDelayed({

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()


            // Your Code
        }, SPLASH_TIME_OUT.toLong())

    }


}