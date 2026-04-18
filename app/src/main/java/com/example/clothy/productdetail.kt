package com.example.clothy

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothy.databinding.ActivityProductdetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class productdetail : AppCompatActivity() {

    private lateinit var binding: ActivityProductdetailBinding
    private val auth = FirebaseAuth.getInstance()
    private val wishlistDb = FirebaseDatabase.getInstance().getReference("Wishlist")
    private val cartDb = FirebaseDatabase.getInstance().getReference("Cart")
    private var isWishlisted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductdetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Get Data from Intent
        val productId = intent.getStringExtra("PRODUCT_ID")
        val name = intent.getStringExtra("PRODUCT_NAME")
        val price = intent.getStringExtra("PRODUCT_PRICE")
        val description = intent.getStringExtra("PRODUCT_DESCRIPTION")
        val image = intent.getStringExtra("PRODUCT_IMAGE")

        // Set Data to UI
        binding.tvTitleDetail.text = name
        binding.tvPriceDetail.text = "₹$price"
        binding.tvDescriptionDetail.text = description

        // Image loading logic
        if (!image.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(image, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                binding.imgDetail.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val userId = auth.currentUser?.uid

        // Check if product is already in wishlist
        if (userId != null && productId != null) {
            wishlistDb.child(userId).child(productId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        isWishlisted = true
                        binding.btnWishlistDetail.setImageResource(R.drawable.ic_heart)
                    } else {
                        isWishlisted = false
                        binding.btnWishlistDetail.setImageResource(R.drawable.ic_heart_outline)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }

        // Wishlist logic
        binding.btnWishlistDetail.setOnClickListener {
            if (userId != null && productId != null) {
                if (isWishlisted) {
                    wishlistDb.child(userId).child(productId).removeValue().addOnSuccessListener {
                        Toast.makeText(this, "Removed from Wishlist", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val productMap = hashMapOf(
                        "id" to productId,
                        "name" to name,
                        "price" to price,
                        "description" to description,
                        "image" to image
                    )
                    wishlistDb.child(userId).child(productId).setValue(productMap).addOnSuccessListener {
                        Toast.makeText(this, "Added to Wishlist", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            }
        }

        // Add to Cart Logic
        binding.btnCartDetail.setOnClickListener {
            if (userId != null && productId != null) {
                val cartItem = hashMapOf(
                    "id" to productId,
                    "name" to name,
                    "price" to price,
                    "image" to image,
                    "quantity" to 1
                )
                cartDb.child(userId).child(productId).setValue(cartItem).addOnSuccessListener {
                    Toast.makeText(this, "Added to Cart!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            }
        }

        // Buy Now Logic: Direct redirect to Checkout
        binding.btnBuyNow.setOnClickListener {
            if (userId != null && productId != null) {
                // To buy now, we temporarily add it to cart and then redirect to checkout
                val cartItem = hashMapOf(
                    "id" to productId,
                    "name" to name,
                    "price" to price,
                    "image" to image,
                    "quantity" to 1
                )
                cartDb.child(userId).child(productId).setValue(cartItem).addOnSuccessListener {
                    val intent = Intent(this, checkout::class.java)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            }
        }

        // Share button logic
        binding.btnShare.setOnClickListener {
            Toast.makeText(this, "Sharing $name", Toast.LENGTH_SHORT).show()
        }
    }
}
