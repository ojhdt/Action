package com.ojhdtapp.action.ui.archive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ojhdtapp.action.databinding.FragmentActionArchiveHistoryContributionCellBinding
import com.ojhdtapp.action.databinding.FragmentActionArchiveHistoryHistoryCellBinding

object ActionArchiveHistoryAdapters {
    class ContributionAdapter :
        ListAdapter<Pair<Int, String>, ContributionAdapter.ContributionViewHolder>(object :
            DiffUtil.ItemCallback<Pair<Int, String>>() {
            override fun areItemsTheSame(
                oldItem: Pair<Int, String>,
                newItem: Pair<Int, String>
            ): Boolean {
                return oldItem.second == newItem.second
            }

            override fun areContentsTheSame(
                oldItem: Pair<Int, String>,
                newItem: Pair<Int, String>
            ): Boolean {
                return oldItem.second == newItem.second
            }
        }) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributionViewHolder {
            val binding = FragmentActionArchiveHistoryContributionCellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
            return ContributionViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ContributionViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        inner class ContributionViewHolder(private val binding: FragmentActionArchiveHistoryContributionCellBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(data: Pair<Int, String>) {
                binding.run {
                    Glide.with(binding.root)
                        .load(data.first)
                        .into(actionArchiveHistoryContributionIcon)
                    actionArchiveHistoryContributionContent.text = data.second
                }
            }
        }
    }

    class HistoryAdapter:ListAdapter<HistoryData,HistoryAdapter.HistoryViewHolder>(object:
        DiffUtil.ItemCallback<HistoryData>() {
        override fun areItemsTheSame(oldItem: HistoryData, newItem: HistoryData): Boolean {
            return oldItem.trigger == newItem.trigger
        }

        override fun areContentsTheSame(oldItem: HistoryData, newItem: HistoryData): Boolean {
            return oldItem.trigger == newItem.trigger
        }
    }) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
            val binding = FragmentActionArchiveHistoryHistoryCellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,false
            )
            return HistoryViewHolder(binding)
        }

        override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
        inner class HistoryViewHolder(private val binding: FragmentActionArchiveHistoryHistoryCellBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(data: HistoryData) {
                binding.run {
                    actionArchiveHistoryCellMonth.text = data.month
                    actionArchiveHistoryCellDay.text = data.day
                    actionArchiveHistoryCellTrigger.text = data.trigger
                }
            }
        }
    }
    data class HistoryData(val month: String, val day: String, val trigger: String)
}