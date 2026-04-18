package com.example.clothy

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.clothy.databinding.ItemProductBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProductAdapter(private val productList: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.tvProductName.text = product.name
        holder.binding.tvProductPrice.text = "₹${product.price}"

        // Load image from Base64
        if (!product.image.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(product.image, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.binding.imgProduct.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Wishlist Logic from Item Product
        holder.binding.btnWishlist.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val db = FirebaseDatabase.getInstance().getReference("Wishlist")
                val productMap = hashMapOf(
                    "id" to product.id,
                    "name" to product.name,
                    "price" to product.price,
                    "description" to product.description,
                    "image" to product.image
                )
                db.child(userId).child(product.id!!).setValue(productMap)
                    .addOnSuccessListener {
                        holder.binding.btnWishlist.setImageResource(R.drawable.ic_heart)
                        Toast.makeText(holder.itemView.context, "Added to Wishlist", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(holder.itemView.context, "Please login first", Toast.LENGTH_SHORT).show()
            }
        }

        // Click listener to redirect to productdetail activity
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, productdetail::class.java)
            intent.putExtra("PRODUCT_ID", product.id)
            intent.putExtra("PRODUCT_NAME", product.name)
            intent.putExtra("PRODUCT_PRICE", product.price)
            intent.putExtra("PRODUCT_DESCRIPTION", product.description)
            intent.putExtra("PRODUCT_IMAGE", product.image)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = productList.size
}
