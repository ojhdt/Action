package com.ojhdtapp.action.ui.archive

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat.setTransitionName
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.ActionSuggestMoreCellBinding
import com.ojhdtapp.action.logic.model.Suggest
import com.ojhdtapp.action.ui.home.ActionAdapters

class SuggestArchiveAdapter(private val listener: SuggestArchiveListener? = null) :
    ListAdapter<Suggest, SuggestArchiveAdapter.SuggestArchiveViewHolder>(object :
        DiffUtil.ItemCallback<Suggest>() {
        override fun areItemsTheSame(oldItem: Suggest, newItem: Suggest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Suggest, newItem: Suggest): Boolean {
            return oldItem.title == newItem.title
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestArchiveViewHolder {
        val binding = ActionSuggestMoreCellBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )
        return SuggestArchiveViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: SuggestArchiveViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SuggestArchiveViewHolder(
        val binding: ActionSuggestMoreCellBinding,
        val listener: SuggestArchiveListener?
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Suggest) {
            binding.run {
                suggestTitle.text = data.title
                suggestSubhead.text = data.subhead
                if (data.imgUrl != null) {
                    Glide.with(binding.root.context)
                        .load(data.imgUrl)
                        .into(suggestImage)
                } else {
                    suggestImage.visibility = View.GONE
                }
                suggestContent.text = data.content
                // Share Element Transition
                val transitionName = binding.root.resources.getString(
                    R.string.suggest_archive_transition_name,
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
                    listener?.onSuggestClick()
                    binding.root.findNavController()
                        .navigate(
                            R.id.action_suggestArchiveFragment_to_suggestContentFragment,
                            bundle, null, extras
                        )
                }
                cardView.setOnClickListener(onClickListener)
                suggestConfirmBtn.setOnClickListener(onClickListener)
            }
        }
    }

    interface SuggestArchiveListener {
        fun onSuggestClick()
    }
}