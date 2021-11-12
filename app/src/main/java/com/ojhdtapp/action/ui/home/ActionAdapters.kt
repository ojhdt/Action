package com.ojhdtapp.action.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.databinding.ActionActionNowCellBinding
import com.ojhdtapp.action.databinding.ActionHeadlineBinding
import com.ojhdtapp.action.databinding.ActionLabelBinding
import com.ojhdtapp.action.databinding.ActionSuggestMoreCellBinding
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.logic.model.Suggest

//Headline

data class HeadlineMessages(val titleA: String, val titleB: String, val titleC: String)

class HeadlineAdapter : ListAdapter<HeadlineMessages, HeadlineViewHolder>(TheSameDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlineViewHolder {
        val binding =
            ActionHeadlineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeadlineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeadlineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class HeadlineViewHolder(binding: ActionHeadlineBinding) : RecyclerView.ViewHolder(binding.root) {
    val titleAView: TextView = binding.actionTitleATextView
    val titleBView: TextView = binding.actionTitleBTextView
    val titleCView: TextView = binding.actionTitleCTextView
    fun bind(data: HeadlineMessages) {
        titleAView.text = data.titleA
        titleBView.text = data.titleB
        titleCView.text = data.titleC
    }
}

//Action now

class ActionNowAdapter :
    ListAdapter<Action, ActionNowViewHolder>(object : DiffUtil.ItemCallback<Action>() {
        override fun areItemsTheSame(oldItem: Action, newItem: Action): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Action, newItem: Action): Boolean {
            return oldItem.title == newItem.title
        }

    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionNowViewHolder {
        val holder = ActionNowViewHolder(
            ActionActionNowCellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        return holder
    }

    override fun onBindViewHolder(holder: ActionNowViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ActionNowViewHolder(val binding: ActionActionNowCellBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Action) {
        binding.run {
            actionContent.text = data.title
            actionSource.text = "${data.source}•${data.timestamp.time}"
            Glide.with(binding.root)
                .load(data.imageID)
                .into(binding.actionImage)
            data.label.forEach {
                actionChips.addView(Chip(binding.root.context).apply {
                    text = it.second
                    it.first?.let {
                        setChipIconResource(it)
                    }
                })
            }
        }
    }
}

class SuggestMoreAdapter :
    ListAdapter<Suggest, SuggestMoreViewHolder>(object : DiffUtil.ItemCallback<Suggest>() {
        override fun areItemsTheSame(oldItem: Suggest, newItem: Suggest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Suggest, newItem: Suggest): Boolean {
            return oldItem.title == newItem.title
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestMoreViewHolder {
        val binding = ActionSuggestMoreCellBinding.inflate(
            LayoutInflater.from(
                parent
                    .context
            ), parent, false
        )
        return SuggestMoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuggestMoreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SuggestMoreViewHolder(val binding: ActionSuggestMoreCellBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Suggest) {
        binding.run {
            suggestTitle.text = data.title
            suggestSubhead.text = data.subhead
            if (data.imgUrl != null) {
                Glide.with(binding.root.context)
                    .load(data.imgUrl)
//                    .placeholder()
                    .into(suggestImage)
            } else {
                suggestImage.visibility = View.GONE
            }
            suggestContent.text = data.content
        }
    }
}

class LabelAdapter(private val label: String = "标题", private val subLabel: String = "附标题") :
    RecyclerView.Adapter<LabelAdapter.LabelViewHolder>() {
    inner class LabelViewHolder(private val binding: ActionLabelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(label: String, subLabel: String) {
            binding.labelText.text = label
            binding.subLabelText.text = subLabel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        return LabelViewHolder(
            ActionLabelBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind(label, subLabel)
    }

    override fun getItemCount(): Int {
        return 1
    }
}


open class TheSameDiffCallBack : DiffUtil.ItemCallback<HeadlineMessages>() {
    override fun areItemsTheSame(oldItem: HeadlineMessages, newItem: HeadlineMessages): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: HeadlineMessages, newItem: HeadlineMessages): Boolean {
        return false
    }

}