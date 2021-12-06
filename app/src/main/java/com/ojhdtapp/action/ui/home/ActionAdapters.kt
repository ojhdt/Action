package com.ojhdtapp.action.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.*
import androidx.core.view.ViewCompat.setTransitionName
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.DateUtil
import com.ojhdtapp.action.DeviceUtil
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.*
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.logic.model.Suggest

//Headline
object ActionAdapters {
    data class HeadlineMessages(val titleA: String, val titleB: String, val titleC: String)

    class HeadlineAdapter :
        ListAdapter<HeadlineMessages, HeadlineViewHolder>(TheSameDiffCallBack()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlineViewHolder {
            val binding =
                ActionHeadlineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            // Set StatusBar Offset
            binding.root.apply {
                val offset = DeviceUtil.getStatusBarHeight(BaseApplication.context)
                (layoutParams as RecyclerView.LayoutParams).topMargin = marginTop + offset
            }
            return HeadlineViewHolder(binding)
        }

        override fun onBindViewHolder(holder: HeadlineViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    class HeadlineViewHolder(binding: ActionHeadlineBinding) :
        RecyclerView.ViewHolder(binding.root) {
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

    class ActionNowAdapter(val listener: ActionNowListener) :
        ListAdapter<Action, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<Action>() {
            override fun areItemsTheSame(oldItem: Action, newItem: Action): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Action, newItem: Action): Boolean {
                return oldItem.title == newItem.title
            }

        }) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == 0) {
                val binding = ActionActionNowEmptyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ActionNowEmptyViewHolder(binding)
            } else {
                val binding = ActionActionNowCellBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ActionNowViewHolder(binding, listener)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (getItemViewType(position) != 0) {
                (holder as ActionNowViewHolder).bind(getItem(position))
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (currentList.size == 1 && currentList[position] == null) 0 else 1
        }
    }

    class ActionNowViewHolder(
        val binding: ActionActionNowCellBinding,
        val listener: ActionNowListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Action) {
            binding.run {
                actionContent.text = data.title
                actionSource.text = "${data.source}•${DateUtil.timeAgo(data.time)}"
                Glide.with(binding.root)
                    .load(data.imageID)
                    .into(binding.actionImage)
                actionChips.removeAllViews()
                data.label?.forEach {
                    actionChips.addView(Chip(binding.root.context).apply {
                        setChipIconResource(it.key)
                        text = it.value
                    })
                }
                // Share Element Transition
                val transitionName = binding.root.resources.getString(
                    R.string.action_transition_name,
                    data.id.toString()
                )
                setTransitionName(
                    binding.cardView,
                    transitionName
                )
                cardView.setOnClickListener {
                    val bundle = bundleOf("ACTION" to data)
                    val actionContentTransitionName =
                        binding.root.resources.getString(R.string.action_content_transition_name)
                    val extras =
                        FragmentNavigatorExtras(binding.cardView to actionContentTransitionName)
                    // Call Listener Fun
                    listener.onActionCLick()
                    binding.root.findNavController()
                        .navigate(
                            R.id.action_actionFragment_to_actionContentFragment,
                            bundle,
                            null,
                            extras
                        )
                }
            }
        }
    }

    class ActionNowEmptyViewHolder(binding: ActionActionNowEmptyBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ActionNowListener {
        fun onActionCLick()
    }

    class SuggestMoreAdapter(private val listener: SuggestMoreListener) :
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
                    parent.context
                ), parent, false
            )
            return SuggestMoreViewHolder(binding, listener)
        }

        override fun onBindViewHolder(holder: SuggestMoreViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    class SuggestMoreViewHolder(
        val binding: ActionSuggestMoreCellBinding,
        val listener: SuggestMoreListener
    ) :
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
                // Share Element Transition
                val transitionName = binding.root.resources.getString(
                    R.string.suggest_transition_name,
                    data.id.toString()
                )
                setTransitionName(
                    binding.cardView,
                    transitionName
                )
                val onClickListener = View.OnClickListener {
                    val bundle = bundleOf("SUGGEST" to data)
                    val suggestContentTransitionName =
                        binding.root.resources.getString(R.string.suggest_content_transition_name)
                    val extras =
                        FragmentNavigatorExtras(binding.cardView to suggestContentTransitionName)
                    listener.onSuggestClick()
                    binding.root.findNavController()
                        .navigate(
                            R.id.action_actionFragment_to_suggestContentFragment,
                            bundle, null, extras
                        )
                }
                cardView.setOnClickListener(onClickListener)
                suggestConfirmBtn.setOnClickListener(onClickListener)
            }
        }
    }

    interface SuggestMoreListener {
        fun onSuggestClick()
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
        override fun areItemsTheSame(
            oldItem: HeadlineMessages,
            newItem: HeadlineMessages
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: HeadlineMessages,
            newItem: HeadlineMessages
        ): Boolean {
            return false
        }
    }
}