package com.example.clothy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.clothy.databinding.ActivitySearchactivityBinding
import com.google.firebase.database.*

class Searchactivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchactivityBinding
    private lateinit var db: DatabaseReference
    private var allProducts = mutableListOf<Product>()
    private var filteredList = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().getReference("Products")

        binding.btnBack.setOnClickListener { finish() }

        setupRecyclerView()
        loadAllProducts()

        // Real-time Search Logic
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(filteredList)
        binding.rvSearch.layoutManager = GridLayoutManager(this, 2)
        binding.rvSearch.adapter = adapter
    }

    private fun loadAllProducts() {
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allProducts.clear()
                for (postSnapshot in snapshot.children) {
                    val product = postSnapshot.getValue(Product::class.java)
                    if (product != null) {
                        allProducts.add(product)
                    }
                }
                // Initially show all or keep it empty until user searches
                filterProducts("") 
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun filterProducts(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            // Option: Show nothing when search is empty for a cleaner look
            binding.layoutNoResults.visibility = View.GONE
        } else {
            for (product in allProducts) {
                // Matching name or category
                if (product.name?.contains(query, ignoreCase = true) == true ||
                    product.category?.contains(query, ignoreCase = true) == true) {
                    filteredList.add(product)
                }
            }
            
            if (filteredList.isEmpty()) {
                binding.layoutNoResults.visibility = View.VISIBLE
            } else {
                binding.layoutNoResults.visibility = View.GONE
            }
        }
        adapter.notifyDataSetChanged()
    }
}
