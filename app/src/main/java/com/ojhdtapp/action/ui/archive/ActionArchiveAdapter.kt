package com.ojhdtapp.action.ui.archive

import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ojhdtapp.action.*
import com.ojhdtapp.action.databinding.FragmentActionArchiveCellBinding
import com.ojhdtapp.action.databinding.FragmentActionArchiveEmptyBinding
import com.ojhdtapp.action.databinding.FragmentSuggestArchiveTabCellBinding
import com.ojhdtapp.action.databinding.FragmentSuggestArchiveTabEmptyBinding
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.logic.model.Suggest
import com.ojhdtapp.action.util.DateUtil
import com.ojhdtapp.action.util.DensityUtil
import java.lang.StringBuilder

class ActionArchiveAdapter(
    private val listener: MyOnClickListener? = null,
    private val emptyBtnListener: MyOnClickListener? = null
) : ListAdapter<Action, RecyclerView.ViewHolder>(object :
    DiffUtil.ItemCallback<Action>() {
    override fun areItemsTheSame(oldItem: Action, newItem: Action): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Action, newItem: Action): Boolean {
        return oldItem.title == newItem.title
    }
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val binding = FragmentActionArchiveEmptyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
            ActionArchiveEmptyViewHolder(binding, emptyBtnListener)
        } else {
            val binding = FragmentActionArchiveCellBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
            ActionArchiveViewHolder(binding, listener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            (holder as ActionArchiveEmptyViewHolder).bind()
        } else {
            (holder as ActionArchiveViewHolder).bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList.size == 1 && currentList[position] == null) 0 else 1
    }

    inner class ActionArchiveViewHolder(
        val binding: FragmentActionArchiveCellBinding,
        private val listener: MyOnClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Action) {
            binding.run {
                actionArchiveTitle.text = data.title
                val labelStr = StringBuilder()
                data.label?.let {
                    it.onEachIndexed { index, entry ->
                        labelStr.append(entry.value)
                        if (index != it.size - 1) {
                            labelStr.append(" ")
                        }
                    }
                }
                if (data.history.isNotEmpty()) {
                    actionArchiveLabel.text = binding.root.resources.getString(
                        R.string.action_archive_time,
                        DateUtil.timeAgo(data.history.last().time)
                    )
                }
                if (data.imageID != null) {
                    Glide.with(binding.root.context)
                        .load(data.imageID)
                        .into(actionArchiveImage)
                } else {
//                    actionArchiveImage.visibility = View.GONE
                }
                // Share Element Transition
                val transitionName = binding.root.resources.getString(
                    R.string.action_archive_transition_name,
                    data.id.toString()
                )
                ViewCompat.setTransitionName(
                    binding.cardView,
                    transitionName
                )
                val onClickListener = View.OnClickListener {
                    val bundle = bundleOf("ACTION" to data)
                    val actionHistoryTransitionName =
                        binding.root.resources.getString(R.string.action_history_transition_name)
                    val extras =
                        FragmentNavigatorExtras(binding.cardView to actionHistoryTransitionName)
                    listener?.onClick()
                    binding.root.findNavController()
                        .navigate(
                            R.id.action_actionArchiveFragment_to_actionArchiveHistoryFragment,
                            bundle, null, extras
                        )
                }
                cardView.setOnClickListener(onClickListener)
            }
        }

    }

    inner class ActionArchiveEmptyViewHolder(
        var binding: FragmentActionArchiveEmptyBinding,
        private val listener: MyOnClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.actionArchiveEmptyBtn.setOnClickListener {
                listener?.onClick()
                binding.root.findNavController().navigate(R.id.action_global_permissionsFragment)
            }
        }
    }

    inner class ActionArchiveSpaceItemDecoration : RecyclerView.ItemDecoration() {
        private val space = DensityUtil.dip2px(BaseApplication.context, 16f)
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.top = space
            val position = parent.getChildAdapterPosition(view)
            // Right
            if (position % 2 == 0) {
                outRect.left = space
                outRect.right = space / 2
            } else {
                outRect.left = space / 2
                outRect.right = space
            }
        }
    }
}