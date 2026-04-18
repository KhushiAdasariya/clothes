package com.example.clothy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.clothy.databinding.FragmentKidsBinding
import com.google.firebase.database.*

class KidsFragment : Fragment() {

    private lateinit var binding: FragmentKidsBinding
    private lateinit var db: DatabaseReference
    private var productList = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKidsBinding.inflate(inflater, container, false)
        
        db = FirebaseDatabase.getInstance().getReference("Products")
        
        setupRecyclerView()
        loadKidsProducts()
        
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(productList)
        binding.rvKidsProducts.layoutManager = GridLayoutManager(context, 2)
        binding.rvKidsProducts.adapter = adapter
    }

    private fun loadKidsProducts() {
        // Query to get only Kids category products
        val query = db.orderByChild("category").equalTo("Kids")
        
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (postSnapshot in snapshot.children) {
                    val product = postSnapshot.getValue(Product::class.java)
                    if (product != null) {
                        productList.add(product)
                    }
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
