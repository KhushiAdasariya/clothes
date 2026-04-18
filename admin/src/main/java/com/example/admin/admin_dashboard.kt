package com.example.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.databinding.ActivityAdminDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class admin_dashboard : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().reference

        // Logout Button
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, admin_login::class.java))
            finish()
        }

        // Add Product Click
        binding.cardAddProduct.setOnClickListener {
            startActivity(Intent(this, add_product::class.java))
        }

        // Manage Inventory / View Products Click
        binding.cardViewProducts.setOnClickListener {
            startActivity(Intent(this, manage_product::class.java))
        }

        // Manage Customer Orders
        binding.cardOrders.setOnClickListener {
            startActivity(Intent(this, manage_order::class.java))
        }
        
        // View Customers Click
        binding.cardUsers.setOnClickListener {
            startActivity(Intent(this, manage_users::class.java))
        }

        // Fetch Dynamic Stats
        loadStats()
    }

    private fun loadStats() {
        val ordersRef = db.child("Orders")
        
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalSales = 0
                var pendingOrders = 0

                for (orderSnap in snapshot.children) {
                    totalSales++
                    val status = orderSnap.child("status").value?.toString() ?: ""
                    if (status != "Delivered") {
                        pendingOrders++
                    }
                }

                // Update UI with real numbers
                binding.tvTotalSales.text = totalSales.toString()
                binding.tvPendingOrders.text = pendingOrders.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@admin_dashboard, "Error loading stats: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
