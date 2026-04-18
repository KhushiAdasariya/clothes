package com.example.clothy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.clothy.databinding.ActivityWishlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class WishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWishlistBinding
    private lateinit var db: DatabaseReference
    private lateinit var cartDb: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var wishlistList = mutableListOf<Product>()
    private lateinit var adapter: WishlistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "Please login to see wishlist", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db = FirebaseDatabase.getInstance().getReference("Wishlist").child(userId)
        cartDb = FirebaseDatabase.getInstance().getReference("Cart").child(userId)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnContinueShopping.setOnClickListener { finish() }

        setupRecyclerView()
        loadWishlistItems()
    }

    private fun setupRecyclerView() {
        adapter = WishlistAdapter(wishlistList,
            onRemoveClick = { product ->
                removeFromWishlist(product)
            },
            onMoveToCartClick = { product ->
                moveToCart(product)
            }
        )
        binding.rvWishlist.layoutManager = GridLayoutManager(this, 2)
        binding.rvWishlist.adapter = adapter
    }

    private fun loadWishlistItems() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                wishlistList.clear()
                for (postSnapshot in snapshot.children) {
                    val product = postSnapshot.getValue(Product::class.java)
                    if (product != null) {
                        wishlistList.add(product)
                    }
                }
                
                binding.tvWishlistCount.text = "${wishlistList.size} Items"
                
                if (wishlistList.isEmpty()) {
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.rvWishlist.visibility = View.GONE
                } else {
                    binding.layoutEmpty.visibility = View.GONE
                    binding.rvWishlist.visibility = View.VISIBLE
                }
                
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WishlistActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun moveToCart(product: Product) {
        val cartItem = hashMapOf(
            "id" to product.id,
            "name" to product.name,
            "price" to product.price,
            "image" to product.image,
            "quantity" to 1
        )

        // Add to Cart
        cartDb.child(product.id!!).setValue(cartItem).addOnSuccessListener {
            // After adding to cart, remove from wishlist
            removeFromWishlist(product)
            Toast.makeText(this, "Moved to Cart", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to move to cart", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeFromWishlist(product: Product) {
        product.id?.let {
            db.child(it).removeValue().addOnSuccessListener {
                // Success message already handled in moveToCart if called from there
            }
        }
    }
}
