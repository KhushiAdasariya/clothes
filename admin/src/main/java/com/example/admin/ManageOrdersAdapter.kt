package com.example.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.ItemManageOrderBinding
import com.google.firebase.database.FirebaseDatabase

class ManageOrdersAdapter(
    private var orderList: List<OrderData>,
    private val onStatusUpdate: (OrderData, String) -> Unit
) : RecyclerView.Adapter<ManageOrdersAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemManageOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemManageOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderList[position]
        val context = holder.itemView.context
        
        holder.binding.tvOrderId.text = "Order #${order.orderId?.takeLast(5)}"
        holder.binding.tvCurrentStatus.text = order.status
        holder.binding.tvOrderAmount.text = "Total Amount: ${order.totalAmount}"
        
        // 1. Fetch Items Details
        val itemsText = order.items?.joinToString { "${it.name} (x${it.quantity})" }
        holder.binding.tvOrderItems.text = itemsText ?: "No items"

        // 2. Fetch User Details from "Users" node
        order.userId?.let { uid ->
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid)
            userRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value?.toString() ?: "N/A"
                    val phone = snapshot.child("mobile").value?.toString() ?: "N/A"
                    val addr = snapshot.child("address").value?.toString() ?: ""
                    val city = snapshot.child("city").value?.toString() ?: ""
                    val pin = snapshot.child("pincode").value?.toString() ?: ""
                    
                    holder.binding.tvCustomerName.text = "Name: $name"
                    holder.binding.tvCustomerPhone.text = "Phone: $phone"
                    holder.binding.tvCustomerAddress.text = "Address: $addr, $city - $pin"
                }
            }
        }

        // 3. Status Update Buttons
        holder.binding.btnProcess.setOnClickListener { onStatusUpdate(order, "Processing") }
        holder.binding.btnShip.setOnClickListener { onStatusUpdate(order, "Shipped") }
        holder.binding.btnDeliver.setOnClickListener { onStatusUpdate(order, "Delivered") }
    }

    override fun getItemCount(): Int = orderList.size
}
