package com.example.clothy

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clothy.databinding.ItemCartBinding

class CartAdapter(
    private var cartList: MutableList<CartItem>,
    private val onQuantityChange: (CartItem, Int) -> Unit,
    private val onRemoveClick: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]
        holder.binding.tvCartName.text = item.name
        holder.binding.tvCartPrice.text = "₹${item.price}"
        holder.binding.tvQuantity.text = item.quantity.toString()

        if (!item.image.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(item.image, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.binding.imgCart.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        holder.binding.btnPlus.setOnClickListener {
            onQuantityChange(item, 1)
        }

        holder.binding.btnMinus.setOnClickListener {
            if (item.quantity > 1) {
                onQuantityChange(item, -1)
            }
        }

        holder.binding.btnRemoveCart.setOnClickListener {
            onRemoveClick(item)
        }
    }

    override fun getItemCount(): Int = cartList.size
}

data class CartItem(
    val id: String? = null,
    val name: String? = null,
    val price: String? = null,
    val image: String? = null,
    var quantity: Int = 1
)
