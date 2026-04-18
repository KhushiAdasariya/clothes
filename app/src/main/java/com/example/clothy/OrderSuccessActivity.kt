package com.example.clothy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clothy.databinding.ActivityOrderSuccessBinding
import kotlin.random.Random

class OrderSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Generating a random Order ID for professional look
        val randomOrderNumber = Random.nextInt(10000, 99999)
        binding.tvOrderNumber.text = "Order ID: #CLOTH-$randomOrderNumber"

        binding.btnViewOrder.setOnClickListener {
            // Redirecting to Main and telling it to show the Order fragment
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("NAVIGATE_TO", "order")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        binding.btnBackToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Prevent user from going back to checkout
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
