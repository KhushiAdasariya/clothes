package com.example.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.databinding.ActivityManageUsersBinding
import com.google.firebase.database.*

class manage_users : AppCompatActivity() {

    private lateinit var binding: ActivityManageUsersBinding
    private lateinit var db: DatabaseReference
    private var userList = mutableListOf<UserData>()
    private lateinit var adapter: ManageUsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().getReference("Users")

        binding.btnBack.setOnClickListener { finish() }

        setupRecyclerView()
        loadUsers()
    }

    private fun setupRecyclerView() {
        adapter = ManageUsersAdapter(userList)
        binding.rvManageUsers.layoutManager = LinearLayoutManager(this)
        binding.rvManageUsers.adapter = adapter
    }

    private fun loadUsers() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val user = postSnapshot.getValue(UserData::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
                
                if (userList.isEmpty()) {
                    Toast.makeText(this@manage_users, "No customers found", Toast.LENGTH_SHORT).show()
                }
                
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@manage_users, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
