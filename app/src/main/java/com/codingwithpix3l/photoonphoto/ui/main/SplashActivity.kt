package com.codingwithpix3l.photoonphoto.ui.main

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.codingwithpix3l.photoonphoto.databinding.ActivitySplashBinding
import com.google.android.gms.ads.MobileAds
import java.util.*

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private var progres = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this){}

        hideActionBar()
        gradientText()
        progress()


    }

    private fun hideActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.hide()
    }

    private fun gradientText() {
        val shader = LinearGradient(
            0f, 0f, 0f,
            binding.titleTxt.textSize,
            Color.parseColor("#FFFFFF"),
            Color.parseColor("#06FEFE"),
            Shader.TileMode.CLAMP
        )
        val shaderSlogan = LinearGradient(
            0f, 0f, 0f,
            binding.sloganTxt.textSize,
            Color.parseColor("#FFFFFF"),
            Color.parseColor("#06FEFE"),
            Shader.TileMode.CLAMP
        )
        binding.titleTxt.paint.shader = shader
        binding.sloganTxt.paint.shader = shaderSlogan
    }

    private fun progress() {
        val timer = Timer()
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                progres++
                binding.loadingPb.progress = progres
                if (progres >= 100) {
                    timer.cancel()
                    openActivity()
                }
            }
        }
        timer.schedule(timerTask, 0, 20)
    }

    private fun openActivity() {
        val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }


}