package com.example.clothy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothy.databinding.ActivityManageAddressBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Manage_Address : AppCompatActivity() {

    private lateinit var binding: ActivityManageAddressBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        binding.btnBack.setOnClickListener { finish() }

        // Link to Add Address Page
        binding.btnAddAddress.setOnClickListener {
            startActivity(Intent(this, AddNewAddress::class.java))
        }

        binding.btnEditAddress.setOnClickListener {
            startActivity(Intent(this, AddNewAddress::class.java))
        }

        binding.btnRemoveAddress.setOnClickListener {
            removeAddress()
        }

        loadUserAddress()
    }

    private fun loadUserAddress() {
        val userId = auth.currentUser?.uid ?: return
        val userRef = db.getReference("Users").child(userId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value?.toString() ?: ""
                    val address = snapshot.child("address").value?.toString() ?: ""
                    val city = snapshot.child("city").value?.toString() ?: ""
                    val state = snapshot.child("state").value?.toString() ?: ""
                    val pin = snapshot.child("pincode").value?.toString() ?: ""
                    val mobile = snapshot.child("mobile").value?.toString() ?: ""

                    if (address.isNotEmpty()) {
                        binding.layoutEmptyAddress.visibility = View.GONE
                        binding.cardSavedAddress.visibility = View.VISIBLE
                        
                        binding.tvUserName.text = name
                        binding.tvFullAddress.text = "$address, $city, $state - $pin"
                        binding.tvMobile.text = "Mobile: +91 $mobile"
                    } else {
                        showEmptyState()
                    }
                } else {
                    showEmptyState()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Manage_Address, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEmptyState() {
        binding.layoutEmptyAddress.visibility = View.VISIBLE
        binding.cardSavedAddress.visibility = View.GONE
    }

    private fun removeAddress() {
        val userId = auth.currentUser?.uid ?: return
        val updates = hashMapOf<String, Any?>(
            "address" to "",
            "city" to "",
            "state" to "",
            "pincode" to ""
        )
        
        db.getReference("Users").child(userId).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Address Removed", Toast.LENGTH_SHORT).show()
            }
    }
}
