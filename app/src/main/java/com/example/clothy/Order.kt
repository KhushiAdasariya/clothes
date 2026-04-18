package com.example.clothy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothy.databinding.FragmentOrderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Order : Fragment() {

    private lateinit var binding: FragmentOrderBinding
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var orderList = mutableListOf<OrderData>()
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        
        if (userId != null) {
            db = FirebaseDatabase.getInstance().getReference("Orders")
            setupRecyclerView()
            loadUserOrders(userId)
        }
        
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = OrderAdapter(orderList)
        binding.rvOrders.layoutManager = LinearLayoutManager(context)
        binding.rvOrders.adapter = adapter
    }

    private fun loadUserOrders(userId: String) {
        // Query to get only orders belonging to this user
        val query = db.orderByChild("userId").equalTo(userId)
        
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                for (postSnapshot in snapshot.children) {
                    val order = postSnapshot.getValue(OrderData::class.java)
                    if (order != null) {
                        orderList.add(order)
                    }
                }
                
                // Sort by timestamp (latest first) if needed
                orderList.reverse()

                if (orderList.isEmpty()) {
                    binding.layoutOrdersEmpty.visibility = View.VISIBLE
                    binding.rvOrders.visibility = View.GONE
                } else {
                    binding.layoutOrdersEmpty.visibility = View.GONE
                    binding.rvOrders.visibility = View.VISIBLE
                }
                
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
