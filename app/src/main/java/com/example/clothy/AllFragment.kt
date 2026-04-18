package com.example.clothy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.clothy.databinding.FragmentAllBinding
import com.google.firebase.database.*

class AllFragment : Fragment() {

    private lateinit var binding: FragmentAllBinding
    private lateinit var db: DatabaseReference
    private var productList = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllBinding.inflate(inflater, container, false)
        
        db = FirebaseDatabase.getInstance().getReference("Products")
        
        setupRecyclerView()
        loadAllProducts()
        setupCategoryClicks()
        
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(productList)
        binding.productRecycler.layoutManager = GridLayoutManager(context, 2)
        binding.productRecycler.adapter = adapter
    }

    private fun loadAllProducts() {
        db.addValueEventListener(object : ValueEventListener {
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

    private fun setupCategoryClicks() {
        binding.catWomen.setOnClickListener { navigateToCategory("Women") }
        binding.catMen.setOnClickListener { navigateToCategory("Men") }
        binding.catKids.setOnClickListener { navigateToCategory("Kids") }
        binding.catNew.setOnClickListener { 
            Toast.makeText(context, "New Arrivals Selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToCategory(category: String) {
        // Logic to switch fragment via Home parent
        val parent = parentFragment as? Home
        parent?.let {
            when(category) {
                "Women" -> it.view?.findViewById<View>(R.id.tvWomen)?.performClick()
                "Men" -> it.view?.findViewById<View>(R.id.tvMen)?.performClick()
                "Kids" -> it.view?.findViewById<View>(R.id.tvKids)?.performClick()
            }
        }
    }
}
