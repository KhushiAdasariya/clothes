package com.example.clothy

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clothy.databinding.ItemWishlistBinding

class WishlistAdapter(
    private var wishlist: MutableList<Product>,
    private val onRemoveClick: (Product) -> Unit,
    private val onMoveToCartClick: (Product) -> Unit
) : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {

    class WishlistViewHolder(val binding: ItemWishlistBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val binding = ItemWishlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WishlistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val product = wishlist[position]
        holder.binding.tvProductName.text = product.name
        holder.binding.tvPrice.text = "₹${product.price}"
        holder.binding.tvBrand.text = "Clothify Exclusive"

        // Decode Base64 image
        if (!product.image.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(product.image, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.binding.imgProduct.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        holder.binding.btnRemove.setOnClickListener {
            onRemoveClick(product)
        }

        holder.binding.btnMoveToCart.setOnClickListener {
            onMoveToCartClick(product)
        }
    }

    override fun getItemCount(): Int = wishlist.size
}
