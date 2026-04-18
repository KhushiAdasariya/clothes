package com.example.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.databinding.ActivityManageProductBinding
import com.google.firebase.database.*

class manage_product : AppCompatActivity() {

    private lateinit var binding: ActivityManageProductBinding
    private lateinit var db: DatabaseReference
    private lateinit var adapter: ManageProductAdapter
    private var productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().getReference("Products")

        binding.btnBack.setOnClickListener { finish() }

        setupRecyclerView()
        loadProducts()
    }

    private fun setupRecyclerView() {
        adapter = ManageProductAdapter(productList, 
            onEditClick = { product ->
                // Send product data to AddProductActivity for editing
                val intent = Intent(this, add_product::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                intent.putExtra("PRODUCT_NAME", product.name)
                intent.putExtra("PRODUCT_PRICE", product.price)
                intent.putExtra("PRODUCT_CATEGORY", product.category)
                intent.putExtra("PRODUCT_DESCRIPTION", product.description)
                intent.putExtra("PRODUCT_IMAGE", product.image)
                intent.putExtra("IS_EDIT", true)
                startActivity(intent)
            },
            onDeleteClick = { product ->
                showDeleteDialog(product)
            }
        )
        binding.rvManageProducts.layoutManager = LinearLayoutManager(this)
        binding.rvManageProducts.adapter = adapter
    }

    private fun loadProducts() {
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
                Toast.makeText(this@manage_product, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteDialog(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete ${product.name}?")
            .setPositiveButton("Delete") { _, _ ->
                product.id?.let {
                    db.child(it).removeValue().addOnSuccessListener {
                        Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
