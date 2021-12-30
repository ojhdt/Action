package com.ojhdtapp.action.ui.welcome

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat.clearColorFilter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ojhdtapp.action.BaseApplication.Companion.context
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.WelcomeSetPermissionCellBinding

class PermissionAdapter :
    ListAdapter<PermissionMessage, PermissionAdapter.PermissionViewHolder>(object :
        DiffUtil.ItemCallback<PermissionMessage>() {
        override fun areItemsTheSame(
            oldItem: PermissionMessage,
            newItem: PermissionMessage
        ): Boolean = false


        override fun areContentsTheSame(
            oldItem: PermissionMessage,
            newItem: PermissionMessage
        ): Boolean = false

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

            binding.permissionState.run {
                if (data.isGranted) {
                    TypedValue().apply {
                        context.theme.resolveAttribute(
                            android.R.attr.colorPrimary,
                            this,
                            true
                        )
                    }.data.let {
                        setColorFilter(
                            it,
                            android.graphics.PorterDuff.Mode.SRC_IN
                        )
                    }
                } else {
                    clearColorFilter()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionViewHolder {
        val binding = WelcomeSetPermissionCellBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
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