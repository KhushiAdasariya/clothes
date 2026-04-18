package com.example.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.databinding.ActivityManageOrderBinding
import com.google.firebase.database.*

class manage_order : AppCompatActivity() {

    private lateinit var binding: ActivityManageOrderBinding
    private lateinit var db: DatabaseReference
    private var orderList = mutableListOf<OrderData>()
    private lateinit var adapter: ManageOrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().getReference("Orders")

        binding.btnBack.setOnClickListener { finish() }

        setupRecyclerView()
        loadAllOrders()
    }

    private fun setupRecyclerView() {
        adapter = ManageOrdersAdapter(orderList) { order, newStatus ->
            updateOrderStatus(order, newStatus)
        }
        binding.rvManageOrders.layoutManager = LinearLayoutManager(this)
        binding.rvManageOrders.adapter = adapter
    }

    private fun loadAllOrders() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                for (postSnapshot in snapshot.children) {
                    val order = postSnapshot.getValue(OrderData::class.java)
                    if (order != null) {
                        orderList.add(order)
                    }
                }
                
                if (orderList.isEmpty()) {
                    binding.layoutNoOrders.visibility = View.VISIBLE
                    binding.rvManageOrders.visibility = View.GONE
                } else {
                    binding.layoutNoOrders.visibility = View.GONE
                    binding.rvManageOrders.visibility = View.VISIBLE
                    orderList.reverse() // Latest first
                }
                
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@manage_order, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateOrderStatus(order: OrderData, newStatus: String) {
        order.orderId?.let { id ->
            db.child(id).child("status").setValue(newStatus)
                .addOnSuccessListener {
                    Toast.makeText(this, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
