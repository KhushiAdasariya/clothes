package com.example.admin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class admin_splash_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_splash_screen)

        val auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            // Check if admin is already logged in
            val currentUser = auth.currentUser
            
            // Check if the logged in user is the admin (based on email)
            if (currentUser != null && currentUser.email == "admin@clothy.com") {
                // Admin is logged in, go to Dashboard
                startActivity(Intent(this, admin_dashboard::class.java))
            } else {
                // Not logged in or not admin, go to Login page
                startActivity(Intent(this, admin_login::class.java))
            }
            finish()
        }, 2500) // 2.5 seconds delay for a premium feel
    }
}
