package com.example.clothy

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothy.databinding.ActivityTrackOrderBinding
import com.google.firebase.database.*

class TrackOrder : AppCompatActivity() {

    private lateinit var binding: ActivityTrackOrderBinding
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val orderId = intent.getStringExtra("ORDER_ID")
        if (orderId == null) {
            finish()
            return
        }

        binding.btnBack.setOnClickListener { finish() }
        
        db = FirebaseDatabase.getInstance().getReference("Orders").child(orderId)
        
        loadTrackingDetails(orderId)
    }

    private fun loadTrackingDetails(orderId: String) {
        binding.tvTrackOrderId.text = "Order ID: #$orderId"
        
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val status = snapshot.child("status").value.toString()
                    updateTimeline(status)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TrackOrder, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateTimeline(status: String) {
        // Reset all to default gray
        val inactiveColor = resources.getColor(R.color.black) // Placeholder or specific gray
        val activeColor = resources.getColor(R.color.black) // Should use Indigo from theme
        
        // Since I can't access colors easily, I'll use hardcoded logic or just tint
        // For professional look, we highlight steps based on status
        
        when (status) {
            "Placed" -> {
                setStepActive(binding.dotPlaced, null, null)
            }
            "Processing" -> {
                setStepActive(binding.dotPlaced, binding.dotProcessing, null)
                binding.tvStepProcessing.setTextColor(resources.getColor(android.R.color.black))
            }
            "Shipped" -> {
                setStepActive(binding.dotPlaced, binding.dotProcessing, binding.dotShipped)
                binding.tvStepProcessing.setTextColor(resources.getColor(android.R.color.black))
                binding.tvStepShipped.setTextColor(resources.getColor(android.R.color.black))
            }
            "Delivered" -> {
                setStepActive(binding.dotPlaced, binding.dotProcessing, binding.dotShipped)
                binding.dotDelivered.backgroundTintList = android.content.res.ColorStateList.valueOf(resources.getColor(android.R.color.holo_green_dark))
                binding.tvStepDelivered.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            }
        }
    }

    private fun setStepActive(vararg views: View?) {
        for (view in views) {
            view?.backgroundTintList = android.content.res.ColorStateList.valueOf(resources.getColor(android.R.color.holo_blue_dark))
        }
    }
}
