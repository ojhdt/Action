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
import com.ojhdtapp.action.DateUtil
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.ActionSuggestMoreCellBinding
import com.ojhdtapp.action.databinding.FragmentSuggestArchiveTabCellBinding
import com.ojhdtapp.action.databinding.FragmentSuggestArchiveTabEmptyBinding
import com.ojhdtapp.action.databinding.FragmentSuggestHistoryTabEmptyBinding
import com.ojhdtapp.action.logic.model.Suggest

class SuggestArchiveAdapter(
    private val listener: SuggestArchiveListener? = null,
    private val emptyBtnListener: SuggestArchiveListener? = null
) : ListAdapter<Suggest, RecyclerView.ViewHolder>(object :
    DiffUtil.ItemCallback<Suggest>() {
    override fun areItemsTheSame(oldItem: Suggest, newItem: Suggest): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Suggest, newItem: Suggest): Boolean {
        return oldItem.title == newItem.title
    }
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val binding = FragmentSuggestArchiveTabEmptyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
            SuggestArchiveEmptyViewHolder(binding, emptyBtnListener)
        } else {
            val binding = FragmentSuggestArchiveTabCellBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
            SuggestArchiveViewHolder(binding, listener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            (holder as SuggestArchiveAdapter.SuggestArchiveEmptyViewHolder).bind()
        } else {
            (holder as SuggestArchiveAdapter.SuggestArchiveViewHolder).bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList.size == 1 && currentList[position] == null) 0 else 1
    }

    inner class SuggestArchiveViewHolder(
        val binding: FragmentSuggestArchiveTabCellBinding,
        private val listener: SuggestArchiveListener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Suggest) {
            binding.run {
                suggestArchiveTitle.text = data.title
                suggestArchiveSource.text = data.source
                if (data.imgUrl != null) {
                    Glide.with(binding.root.context)
                        .load(data.imgUrl)
                        .into(suggestArchiveImage)
                } else {
                    suggestArchiveImage.visibility = View.GONE
                }
                suggestArchiveTime.text = DateUtil.formatDate(data.time)
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
            }
        }

    }

    inner class SuggestArchiveEmptyViewHolder(
        var binding: FragmentSuggestArchiveTabEmptyBinding,
        private val listener: SuggestArchiveListener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.suggestArchiveEmptyBtn.setOnClickListener {
                listener?.onSuggestClick()
            }
        }
    }

    interface SuggestArchiveListener {
        fun onSuggestClick()
    }
}