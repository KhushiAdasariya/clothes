package com.example.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.databinding.ActivityAdminLoginBinding
import com.google.firebase.auth.FirebaseAuth

class admin_login : AppCompatActivity() {

    private lateinit var binding: ActivityAdminLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Hardcoded Credentials
        val correctAdminEmail = "admin@clothy.com"
        val correctAdminPass = "admin123" 

        binding.etAdminEmail.setText(correctAdminEmail)
        binding.etAdminPassword.setText(correctAdminPass)

        binding.btnAdminLogin.setOnClickListener {
            val email = binding.etAdminEmail.text.toString().trim()
            val password = binding.etAdminPassword.text.toString().trim()

            if (email == correctAdminEmail && password == correctAdminPass) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnAdminLogin.isEnabled = false

                // Direct Navigation without waiting too long
                Toast.makeText(this@admin_login, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@admin_login, admin_dashboard::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
