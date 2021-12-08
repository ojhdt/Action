package com.ojhdtapp.action.ui.archive

import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ojhdtapp.action.DateUtil
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentActionArchiveCellBinding
import com.ojhdtapp.action.logic.model.Suggest

class ActionArchiveAdapter {
    inner class ActionArchiveViewHolder(
        val binding: FragmentActionArchiveCellBinding,
        private val listener: SuggestArchiveListener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Suggest) {
            binding.run {
                actionArchiveTitle.text = data.title
                acti.text = data.source
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
                ViewCompat.setTransitionName(
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