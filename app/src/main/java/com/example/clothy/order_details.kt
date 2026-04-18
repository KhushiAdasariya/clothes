package com.example.clothy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothy.databinding.ActivityOrderDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class order_details : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var orderItems = mutableListOf<CartItem>()
    private lateinit var adapter: OrderProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        val orderId = intent.getStringExtra("ORDER_ID")

        binding.btnBack.setOnClickListener { finish() }

        if (orderId != null && userId != null) {
            setupRecyclerView()
            loadOrderDetails(orderId)
        } else {
            Toast.makeText(this, "Order ID not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnDownloadInvoice.setOnClickListener {
            Toast.makeText(this, "Invoice downloading...", Toast.LENGTH_SHORT).show()
        }

        binding.btnShopMore.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderProductsAdapter(orderItems)
        binding.rvOrderItemsList.layoutManager = LinearLayoutManager(this)
        binding.rvOrderItemsList.adapter = adapter
    }

    private fun loadOrderDetails(orderId: String) {
        db = FirebaseDatabase.getInstance().getReference("Orders").child(orderId)
        
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val orderAmount = snapshot.child("totalAmount").value?.toString() ?: "₹0"
                    val status = snapshot.child("status").value?.toString() ?: "Placed"
                    
                    binding.tvOrderId.text = "Order ID: #$orderId"
                    binding.tvOrderStatus.text = "STATUS: ${status.uppercase()}"
                    binding.tvTotalPaid.text = orderAmount
                    binding.tvSubtotal.text = orderAmount
                    
                    // Critical Fix: Loading items list from Firebase snapshot
                    orderItems.clear()
                    val itemsSnapshot = snapshot.child("items")
                    for (itemSnap in itemsSnapshot.children) {
                        val item = itemSnap.getValue(CartItem::class.java)
                        if (item != null) {
                            orderItems.add(item)
                        }
                    }
                    adapter.notifyDataSetChanged()
                    
                    // Fetch user address to show in details
                    fetchUserAddress()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@order_details, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUserAddress() {
        val userId = auth.currentUser?.uid ?: return
        FirebaseDatabase.getInstance().getReference("Users").child(userId).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value?.toString() ?: "User"
                    val mobile = snapshot.child("mobile").value?.toString() ?: ""
                    val address = snapshot.child("address").value?.toString() ?: ""
                    val city = snapshot.child("city").value?.toString() ?: ""
                    val state = snapshot.child("state").value?.toString() ?: ""
                    val pin = snapshot.child("pincode").value?.toString() ?: ""
                    
                    binding.tvShippingName.text = name
                    binding.tvShippingAddress.text = "$address, $city, $state - $pin\nMobile: $mobile"
                }
            }
    }
}
