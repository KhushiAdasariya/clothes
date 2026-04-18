package com.example.admin

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.ItemManageUserBinding

class ManageUsersAdapter(private val userList: List<UserData>) : RecyclerView.Adapter<ManageUsersAdapter.UserViewHolder>() {

    class UserViewHolder(val binding: ItemManageUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemManageUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.tvUserName.text = user.name ?: "Unknown User"
        holder.binding.tvUserEmail.text = user.email ?: "No Email"
        holder.binding.tvUserPhone.text = user.mobile ?: "No Mobile"

        if (!user.profileImage.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(user.profileImage, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.binding.imgUserAvatar.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int = userList.size
}
