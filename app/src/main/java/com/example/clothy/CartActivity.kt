package com.example.clothy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothy.databinding.ActivityCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var cartList = mutableListOf<CartItem>()
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db = FirebaseDatabase.getInstance().getReference("Cart").child(userId)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnShopNow.setOnClickListener { finish() }

        setupRecyclerView()
        loadCartItems()

        // Redirect to Checkout
        binding.btnCheckout.setOnClickListener {
            if (cartList.isNotEmpty()) {
                val intent = Intent(this, checkout::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter(cartList,
            onQuantityChange = { item, change ->
                updateQuantity(item, change)
            },
            onRemoveClick = { item ->
                removeFromCart(item)
            }
        )
        binding.rvCart.layoutManager = LinearLayoutManager(this)
        binding.rvCart.adapter = adapter
    }

    private fun loadCartItems() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartList.clear()
                var total = 0
                for (postSnapshot in snapshot.children) {
                    val item = postSnapshot.getValue(CartItem::class.java)
                    if (item != null) {
                        cartList.add(item)
                        val price = item.price?.replace(",", "")?.replace("₹", "")?.trim()?.toIntOrNull() ?: 0
                        total += price * item.quantity
                    }
                }

                updateUI(total)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CartActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(total: Int) {
        if (cartList.isEmpty()) {
            binding.layoutCartEmpty.visibility = View.VISIBLE
            binding.rvCart.visibility = View.GONE
            binding.layoutCheckout.visibility = View.GONE
            binding.tvCartCount.text = "0 Items"
        } else {
            binding.layoutCartEmpty.visibility = View.GONE
            binding.rvCart.visibility = View.VISIBLE
            binding.layoutCheckout.visibility = View.VISIBLE
            binding.tvCartCount.text = "${cartList.size} Items"
            
            binding.tvSubtotal.text = "₹$total"
            
            val deliveryCharge = if (total >= 599) 0 else 50
            
            if (deliveryCharge == 0) {
                binding.tvShippingFee.text = "FREE"
                binding.tvShippingFee.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            } else {
                binding.tvShippingFee.text = "₹$deliveryCharge"
                binding.tvShippingFee.setTextColor(resources.getColor(android.R.color.black))
            }

            val finalTotal = total + deliveryCharge
            binding.tvTotalCartAmount.text = "₹$finalTotal"
            binding.tvFinalCartPrice.text = "₹$finalTotal"
        }
    }

    private fun updateQuantity(item: CartItem, change: Int) {
        val newQty = item.quantity + change
        if (newQty > 0) {
            db.child(item.id!!).child("quantity").setValue(newQty)
        }
    }

    private fun removeFromCart(item: CartItem) {
        db.child(item.id!!).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Removed from cart", Toast.LENGTH_SHORT).show()
        }
    }
}
