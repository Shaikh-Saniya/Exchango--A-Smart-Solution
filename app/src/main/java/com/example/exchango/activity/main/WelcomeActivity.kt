package com.example.exchango.activity.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.exchango.R
import com.example.exchango.databinding.ActivityWelcomeBinding
import androidx.appcompat.app.AppCompatDelegate
import com.example.exchango.activity.userprofile.SignIn

class WelcomeActivity : AppCompatActivity() {

    lateinit var welcome: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        welcome = ActivityWelcomeBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        setContentView(welcome.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val riveAnimationView = welcome.riveView
        riveAnimationView.play()

        welcome.signUp.setOnClickListener {
            Log.d("check","joinUsButton clicked")
            val intent = Intent(this@WelcomeActivity,SignIn::class.java)
            startActivity(intent)
        }
        welcome.signIn.setOnClickListener {
            Log.d("check","joinUsButton clicked")
            val intent = Intent(this@WelcomeActivity,SignIn::class.java)
            startActivity(intent)
        }
    }
}
