package com.example.clothy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothy.databinding.ActivityCheckoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class checkout : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private var cartList = mutableListOf<CartItem>()
    private lateinit var adapter: OrderProductsAdapter
    private var isAddressAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid

        if (userId == null) {
            finish()
            return
        }

        binding.btnBack.setOnClickListener { finish() }
        
        binding.btnEditAddressCheckout.setOnClickListener {
            startActivity(Intent(this, Manage_Address::class.java))
        }

        setupRecyclerView()
        loadUserData(userId)
        loadCartData(userId)

        binding.btnConfirmOrder.setOnClickListener {
            if (isAddressAvailable) {
                placeOrder(userId)
            } else {
                Toast.makeText(this, "Please add your shipping address first", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderProductsAdapter(cartList)
        binding.rvOrderItems.layoutManager = LinearLayoutManager(this)
        binding.rvOrderItems.adapter = adapter
    }

    private fun loadUserData(userId: String) {
        db.getReference("Users").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value?.toString() ?: "User"
                    val mobile = snapshot.child("mobile").value?.toString() ?: ""
                    val address = snapshot.child("address").value?.toString() ?: ""
                    val city = snapshot.child("city").value?.toString() ?: ""
                    val state = snapshot.child("state").value?.toString() ?: ""
                    val pincode = snapshot.child("pincode").value?.toString() ?: ""

                    binding.tvAddressName.text = name
                    binding.tvAddressMobile.text = "+91 $mobile"
                    
                    if (address.isNotEmpty()) {
                        binding.tvFullAddress.text = "$address, $city, $state - $pincode"
                        binding.tvFullAddress.setTextColor(resources.getColor(android.R.color.black))
                        isAddressAvailable = true
                    } else {
                        binding.tvFullAddress.text = "No address added yet. Click EDIT to add."
                        binding.tvFullAddress.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                        isAddressAvailable = false
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadCartData(userId: String) {
        db.getReference("Cart").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartList.clear()
                var subtotal = 0
                for (postSnapshot in snapshot.children) {
                    val item = postSnapshot.getValue(CartItem::class.java)
                    if (item != null) {
                        cartList.add(item)
                        val cleanPrice = item.price?.replace(",", "")?.replace("₹", "")?.trim()?.toIntOrNull() ?: 0
                        subtotal += cleanPrice * item.quantity
                    }
                }
                calculateFinalPrice(subtotal)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun calculateFinalPrice(subtotal: Int) {
        val shipping = if (subtotal >= 599 || subtotal == 0) 0 else 50
        val total = subtotal + shipping

        binding.tvOrderTotal.text = "₹$subtotal"
        binding.tvShippingFee.text = if (shipping == 0) "FREE" else "₹$shipping"
        binding.tvPayableAmount.text = "₹$total"
        binding.tvFinalAmount.text = "₹$total"
    }

    private fun placeOrder(userId: String) {
        if (cartList.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnConfirmOrder.isEnabled = false

        val orderId = db.getReference("Orders").push().key ?: ""
        val paymentMethod = if (binding.rbCOD.isChecked) "COD" else "Online"
        
        // FIXED: Now including totalAmount so Order Details won't show null
        val orderMap = hashMapOf(
            "orderId" to orderId,
            "userId" to userId,
            "items" to cartList,
            "totalAmount" to binding.tvFinalAmount.text.toString(),
            "paymentMethod" to paymentMethod,
            "status" to "Placed",
            "timestamp" to ServerValue.TIMESTAMP
        )

        db.getReference("Orders").child(orderId).setValue(orderMap).addOnSuccessListener {
            db.getReference("Cart").child(userId).removeValue().addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                val intent = Intent(this, OrderSuccessActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener {
            binding.progressBar.visibility = View.GONE
            binding.btnConfirmOrder.isEnabled = true
            Toast.makeText(this, "Order Failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
