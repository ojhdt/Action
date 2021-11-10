package com.ojhdtapp.action.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ojhdtapp.action.databinding.ActionHeadlineBinding

data class HeadlineMessages(val titleA:String, val titleB:String, val titleC:String)

class HeadlineAdapter : ListAdapter<HeadlineMessages,HeadlineViewHolder>(TheSameDiffCallBack()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlineViewHolder {
        val binding = ActionHeadlineBinding.inflate(LayoutInflater.from(parent.context))
        return HeadlineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeadlineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class HeadlineViewHolder(binding: ActionHeadlineBinding) : RecyclerView.ViewHolder(binding.root){
    val titleAView:TextView = binding.actionTitleATextView
    val titleBView:TextView = binding.actionTitleBTextView
    val titleCView:TextView = binding.actionTitleCTextView
    fun bind(data:HeadlineMessages){
        titleAView.text = data.titleA
        titleBView.text = data.titleB
        titleCView.text = data.titleC
    }
}

open class TheSameDiffCallBack : DiffUtil.ItemCallback<HeadlineMessages>(){
    override fun areItemsTheSame(oldItem: HeadlineMessages, newItem: HeadlineMessages): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: HeadlineMessages, newItem: HeadlineMessages): Boolean {
        return false
    }

}