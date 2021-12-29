package com.ojhdtapp.action.ui.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ojhdtapp.action.databinding.FragmentActionContentHighlightsCellBinding

class ActionContentHighlightAdapter :
    ListAdapter<Pair<Int, String>, ActionContentHighlightAdapter.HightlightViewHolder>(object :
        DiffUtil.ItemCallback<Pair<Int, String>>() {
        override fun areItemsTheSame(
            oldItem: Pair<Int, String>,
            newItem: Pair<Int, String>
        ): Boolean =
            oldItem.first == newItem.first

        override fun areContentsTheSame(
            oldItem: Pair<Int, String>,
            newItem: Pair<Int, String>
        ): Boolean =
            oldItem.second == newItem.second
    }) {
    inner class HightlightViewHolder(val binding: FragmentActionContentHighlightsCellBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Pair<Int, String>) {
            Glide.with(binding.root)
                .load(data.first)
                .into(binding.highlightCellIcon)
            binding.highlightCellText.text = data.second
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HightlightViewHolder {
        val binding = FragmentActionContentHighlightsCellBinding.inflate(LayoutInflater.from(parent.context),
        parent, false)
        return HightlightViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HightlightViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}