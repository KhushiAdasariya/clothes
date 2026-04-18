package com.example.admin

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.ItemManageProductBinding

class ManageProductAdapter(
    private var productList: MutableList<Product>,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ManageProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemManageProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemManageProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.tvManageName.text = product.name
        holder.binding.tvManagePrice.text = "₹${product.price}"
        holder.binding.tvManageCategory.text = product.category

        // Decode Base64 image
        if (!product.image.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(product.image, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.binding.imgManageProduct.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        holder.binding.btnEditProduct.setOnClickListener {
            onEditClick(product)
        }

        holder.binding.btnDeleteProduct.setOnClickListener {
            onDeleteClick(product)
        }
    }

    override fun getItemCount(): Int = productList.size

    fun updateList(newList: List<Product>) {
        productList.clear()
        productList.addAll(newList)
        notifyDataSetChanged()
    }
}
