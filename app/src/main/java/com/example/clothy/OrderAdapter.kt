package com.example.clothy

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clothy.databinding.ItemOrderBinding

class OrderAdapter(private var orderList: List<OrderData>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.binding.tvOrderId.text = "Order #${order.orderId?.takeLast(5)}"
        holder.binding.tvOrderAmount.text = order.totalAmount
        holder.binding.tvOrderStatus.text = order.status

        // Display items summary
        val itemsText = order.items?.joinToString { "${it.name} x${it.quantity}" }
        holder.binding.tvOrderItems.text = itemsText

        // Link to Track Order Page
        holder.binding.btnTrackOrder.setOnClickListener {
            val intent = Intent(it.context, TrackOrder::class.java)
            intent.putExtra("ORDER_ID", order.orderId)
            it.context.startActivity(intent)
        }

        // Card click still goes to Order Details
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, order_details::class.java)
            intent.putExtra("ORDER_ID", order.orderId)
            intent.putExtra("TOTAL_AMOUNT", order.totalAmount)
            intent.putExtra("STATUS", order.status)
            intent.putExtra("TIMESTAMP", order.timestamp)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = orderList.size
}

data class OrderData(
    val orderId: String? = null,
    val userId: String? = null,
    val items: List<CartItem>? = null,
    val totalAmount: String? = null,
    val paymentMethod: String? = null,
    val status: String? = "Placed",
    val timestamp: Long? = null
)
