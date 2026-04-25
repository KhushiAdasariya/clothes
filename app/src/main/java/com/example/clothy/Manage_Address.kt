package com.example.clothy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clothy.databinding.ActivityManageAddressBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Manage_Address : AppCompatActivity() {

    private lateinit var binding: ActivityManageAddressBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private var addressList = mutableListOf<AddressModel>()
    private lateinit var adapter: AddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnAddAddress.setOnClickListener {
            startActivity(Intent(this, AddNewAddress::class.java))
        }

        setupRecyclerView()
        loadAddresses()
    }

    private fun setupRecyclerView() {
        adapter = AddressAdapter(
            addressList,
            onEditClick = { address ->
                val intent = Intent(this, EditAddress::class.java)
                intent.putExtra("ADDRESS_ID", address.id)
                startActivity(intent)
            },
            onDeleteClick = { address ->
                deleteAddress(address.id!!)
            },
            onSetDefaultClick = { address ->
                setDefaultAddress(address.id!!)
            }
        )
        binding.rvAddresses.layoutManager = LinearLayoutManager(this)
        binding.rvAddresses.adapter = adapter
    }

    private fun loadAddresses() {
        val userId = auth.currentUser?.uid ?: return
        val ref = db.getReference("Users").child(userId).child("addresses")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                addressList.clear()
                for (snap in snapshot.children) {
                    val address = snap.getValue(AddressModel::class.java)
                    if (address != null) {
                        addressList.add(address)
                    }
                }
                
                if (addressList.isEmpty()) {
                    binding.layoutEmptyAddress.visibility = View.VISIBLE
                    binding.rvAddresses.visibility = View.GONE
                } else {
                    binding.layoutEmptyAddress.visibility = View.GONE
                    binding.rvAddresses.visibility = View.VISIBLE
                }
                
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setDefaultAddress(addressId: String) {
        val userId = auth.currentUser?.uid ?: return
        val ref = db.getReference("Users").child(userId).child("addresses")

        ref.get().addOnSuccessListener { snapshot ->
            val updates = mutableMapOf<String, Any?>()
            for (snap in snapshot.children) {
                val id = snap.key ?: continue
                // 'default' key વાપરી રહ્યા છીએ
                updates["$id/default"] = (id == addressId)
            }
            
            ref.updateChildren(updates).addOnSuccessListener {
                Toast.makeText(this, "Default address set successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteAddress(addressId: String) {
        val userId = auth.currentUser?.uid ?: return
        db.getReference("Users").child(userId).child("addresses").child(addressId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Address deleted", Toast.LENGTH_SHORT).show()
            }
    }
}
