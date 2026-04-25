package com.example.clothy

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.clothy.databinding.ActivityNewarrivalBinding
import com.google.firebase.database.*

class newarrival : AppCompatActivity() {

    private lateinit var binding: ActivityNewarrivalBinding
    private lateinit var db: DatabaseReference
    private var productList = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewarrivalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().getReference("Products")

        binding.btnBack.setOnClickListener { finish() }

        setupRecyclerView()
        loadNewArrivals()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(productList)
        binding.rvNewArrivals.layoutManager = GridLayoutManager(this, 2)
        binding.rvNewArrivals.adapter = adapter
    }

    private fun loadNewArrivals() {
        // Logic: Fetch all products ordered by timestamp and show only the latest ones
        // This will show products from ANY category (Men, Women, Kids) that were added recently
        db.orderByChild("timestamp").limitToLast(20)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    productList.clear()
                    for (postSnapshot in snapshot.children) {
                        val product = postSnapshot.getValue(Product::class.java)
                        if (product != null) {
                            productList.add(product)
                        }
                    }
                    
                    // Reverse the list to show the newest at the top
                    productList.reverse()

                    if (productList.isEmpty()) {
                        binding.layoutEmpty.visibility = View.VISIBLE
                        binding.rvNewArrivals.visibility = View.GONE
                    } else {
                        binding.layoutEmpty.visibility = View.GONE
                        binding.rvNewArrivals.visibility = View.VISIBLE
                    }
                    
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@newarrival, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
