package com.example.clothy

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clothy.databinding.ItemOrderProductBinding

class OrderProductsAdapter(private val itemList: List<CartItem>) : RecyclerView.Adapter<OrderProductsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemOrderProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.tvProductName.text = item.name
        holder.binding.tvProductPrice.text = "₹${item.price}"
        holder.binding.tvQuantity.text = "Qty: ${item.quantity}"

        if (!item.image.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(item.image, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.binding.imgProduct.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int = itemList.size
}
