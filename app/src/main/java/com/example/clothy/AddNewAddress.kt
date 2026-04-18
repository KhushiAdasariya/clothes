package com.example.clothy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothy.databinding.ActivityAddNewAddressBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddNewAddress : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewAddressBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        // Load existing user info (Name and Mobile)
        loadUserInfo()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnSaveAddress.setOnClickListener {
            saveAddress()
        }
    }

    private fun loadUserInfo() {
        val userId = auth.currentUser?.uid ?: return
        
        db.getReference("Users").child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val name = snapshot.child("name").value?.toString() ?: ""
                val mobile = snapshot.child("mobile").value?.toString() ?: ""
                
                binding.etFullName.setText(name)
                binding.etMobile.setText(mobile)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load user info", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveAddress() {
        val userId = auth.currentUser?.uid ?: return
        
        val name = binding.etFullName.text.toString().trim()
        val mobile = binding.etMobile.text.toString().trim()
        val pincode = binding.etPincode.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val state = binding.etState.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val locality = binding.etLocality.text.toString().trim()
        
        val type = if (binding.rbHome.isChecked) "Home" else "Work"

        if (name.isEmpty() || mobile.isEmpty() || pincode.isEmpty() || city.isEmpty() || state.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (mobile.length != 10) {
            Toast.makeText(this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show()
            return
        }

        val fullAddress = "$address, $locality"
        
        val addressMap = hashMapOf(
            "name" to name,
            "mobile" to mobile,
            "address" to fullAddress,
            "city" to city,
            "state" to state,
            "pincode" to pincode,
            "addressType" to type
        )

        db.getReference("Users").child(userId).updateChildren(addressMap as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Address Added Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add address", Toast.LENGTH_SHORT).show()
            }
    }
}
