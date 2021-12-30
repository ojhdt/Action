package com.ojhdtapp.action.ui.welcome

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.WelcomeSetPermissionCellBinding

class PermissionAdapter {
    inner class PermissionViewHolder(val binding: WelcomeSetPermissionCellBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PermissionMessage) {
            binding.permissionTitle.text = data.name
            binding.permissionUsage.text = data.usage
            Glide.with(binding.root)
                .load(data.icon)
                .into(binding.permissionIcon)
            Glide.with(binding.root)
                .load(
                    if (data.isGranted) ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_outline_done_24
                    )
                    else ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_outline_error_outline_24
                    )
                )
                .into(binding.permissionState)
        }
    }
}

data class PermissionMessage(
    val name: String,
    val usage: String,
    val icon: Int,
    var isGranted: Boolean
)