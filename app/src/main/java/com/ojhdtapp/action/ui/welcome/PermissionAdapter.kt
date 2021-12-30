package com.ojhdtapp.action.ui.welcome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.WelcomeSetPermissionCellBinding

class PermissionAdapter :
    ListAdapter<PermissionMessage, PermissionAdapter.PermissionViewHolder>(object :
        DiffUtil.ItemCallback<PermissionMessage>() {
        override fun areItemsTheSame(
            oldItem: PermissionMessage,
            newItem: PermissionMessage
        ): Boolean = oldItem.name == newItem.name


        override fun areContentsTheSame(
            oldItem: PermissionMessage,
            newItem: PermissionMessage
        ): Boolean =
            oldItem.usage == newItem.usage

    }) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionViewHolder {
        val binding = WelcomeSetPermissionCellBinding.inflate(LayoutInflater.from(parent.context),
        parent, false)
        return PermissionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PermissionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

data class PermissionMessage(
    val name: String,
    val usage: String,
    val icon: Int,
    var isGranted: Boolean
)