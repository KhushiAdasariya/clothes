package com.example.clothy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.clothy.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Google Sign-In Configuration
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Normal Email Login
        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            binding.btnLogin.isEnabled = false 
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    binding.progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        goToHome()
                    } else {
                        binding.btnLogin.isEnabled = true
                        val error = task.exception?.message
                        Toast.makeText(this, "Login Failed: $error", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Google Login Button Click
        binding.btnGoogleLogin.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                googleLauncher.launch(signInIntent)
            }
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
    }

    private val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        binding.progressBar.visibility = View.VISIBLE
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userMap = hashMapOf(
                        "name" to (user?.displayName ?: ""),
                        "email" to (user?.email ?: ""),
                        "uid" to user?.uid
                    )
                    
                    // Saving Google User details to Realtime Database so Profile can show it
                    user?.uid?.let {
                        FirebaseDatabase.getInstance().getReference("Users").child(it)
                            .updateChildren(userMap as Map<String, Any>)
                            .addOnCompleteListener {
                                binding.progressBar.visibility = View.GONE
                                goToHome()
                            }
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Google Auth Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun goToHome() {
        Toast.makeText(this, "Welcome to CLOTHIFY!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
