package com.example.clothy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clothy.databinding.ItemAddressBinding

class AddressAdapter(
    private val addressList: List<AddressModel>,
    private val onEditClick: (AddressModel) -> Unit,
    private val onDeleteClick: (AddressModel) -> Unit,
    private val onSetDefaultClick: (AddressModel) -> Unit
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    class AddressViewHolder(val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addressList[position]
        
        holder.binding.apply {
            tvUserName.text = address.name
            tvFullAddress.text = "${address.address}, ${address.locality}, ${address.city}, ${address.state} - ${address.pincode}"
            tvMobile.text = "Mobile: +91 ${address.mobile}"
            tvLabel.text = address.addressType?.uppercase() ?: "HOME"
            
            // Show/Hide Default Badge based on 'default' boolean
            if (address.default) {
                tvDefaultBadge.visibility = View.VISIBLE
                btnSetDefault.visibility = View.GONE
            } else {
                tvDefaultBadge.visibility = View.GONE
                btnSetDefault.visibility = View.VISIBLE
            }

            btnEditAddress.setOnClickListener { onEditClick(address) }
            btnRemoveAddress.setOnClickListener { onDeleteClick(address) }
            btnSetDefault.setOnClickListener { onSetDefaultClick(address) }
        }
    }

    override fun getItemCount(): Int = addressList.size
}
