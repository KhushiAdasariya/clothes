package com.example.clothy

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is already logged in
            if (auth.currentUser != null) {
                // User is signed in, go to Home (MainActivity)
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // No user is signed in, go to Login
                startActivity(Intent(this, Login::class.java))
            }
            finish()
        }, 2500) // 2.5 seconds
    }
}
