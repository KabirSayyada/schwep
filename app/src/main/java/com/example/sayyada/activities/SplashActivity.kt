package com.example.sayyada.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.sayyada.databinding.ActivitySplashBinding
import com.example.sayyada.firebase.FirestoreClass

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val typeFace: Typeface = Typeface.createFromAsset(assets, "wintermesh/Wintermesh Black.ttf")
        binding.splashScreen.typeface = typeFace

        Handler(Looper.myLooper()!!).postDelayed({

            var currentUserID = FirestoreClass().getCurrentUserID()
            if (currentUserID.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            }else
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        },3500)


    }


}

