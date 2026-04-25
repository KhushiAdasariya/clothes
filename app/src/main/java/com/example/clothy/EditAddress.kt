package com.example.clothy

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothy.databinding.ActivityEditAddressBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditAddress : AppCompatActivity() {

    private lateinit var binding: ActivityEditAddressBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private var addressId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        // Get Address ID from Intent
        addressId = intent.getStringExtra("ADDRESS_ID")

        if (addressId != null) {
            loadAddressDetails(addressId!!)
        } else {
            Toast.makeText(this, "Error: Address not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnUpdateAddress.setOnClickListener {
            updateAddress()
        }
    }

    private fun loadAddressDetails(id: String) {
        val userId = auth.currentUser?.uid ?: return
        db.getReference("Users").child(userId).child("addresses").child(id).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val address = snapshot.getValue(AddressModel::class.java)
                    address?.let {
                        binding.etFullName.setText(it.name)
                        binding.etMobile.setText(it.mobile)
                        binding.etPincode.setText(it.pincode)
                        binding.etCity.setText(it.city)
                        binding.etState.setText(it.state)
                        binding.etAddress.setText(it.address)
                        binding.etLocality.setText(it.locality)
                        if (it.addressType == "Work") binding.rbWork.isChecked = true else binding.rbHome.isChecked = true
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load address", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateAddress() {
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
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = db.getReference("Users").child(userId).child("addresses").child(addressId!!)
        
        val addressMap = hashMapOf(
            "name" to name,
            "mobile" to mobile,
            "address" to address,
            "locality" to locality,
            "city" to city,
            "state" to state,
            "pincode" to pincode,
            "addressType" to type
        )

        ref.updateChildren(addressMap as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Address Updated Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update address", Toast.LENGTH_SHORT).show()
            }
    }
}
