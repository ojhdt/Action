package com.ojhdtapp.action.ui.home

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.DrawableUtils
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.DensityUtil
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.AchievementMediumCardBinding
import com.ojhdtapp.action.databinding.AchievementSmallCardBinding
import com.ojhdtapp.action.databinding.AchievementXpBinding
import com.ojhdtapp.action.logic.model.StatisticsBlock

object AchievementAdapters {
    class StatisticsAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var totalTitleA: String =
            BaseApplication.context.getString(R.string.achievement_total_a)
        private var totalNum: Int = 0
        private var totalTitleC: String =
            BaseApplication.context.getString(R.string.achievement_total_c)
        private var list: MutableList<StatisticsBlock> = mutableListOf()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            when (viewType) {
                0 -> {
                    val binding =
                        AchievementMediumCardBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    return TotalViewHolder(binding)
                }
                else -> {
                    val binding = AchievementSmallCardBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return StatisticsViewHolder(binding)
                }
            }

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (getItemViewType(position)) {
                0 -> {
                    (holder as TotalViewHolder).bind(totalTitleA, totalNum, totalTitleC)
                }
                else -> {
                    val item = list[position - 1]
                    (holder as StatisticsViewHolder).bind(item)
                }
            }
        }

        override fun getItemCount(): Int {
            return list.size + 1
        }

        override fun getItemViewType(position: Int): Int {
            return when (position) {
                0 -> 0
                else -> 1
            }
        }

        fun setTotalNum(num: Int) {
            totalNum = num
            notifyDataSetChanged()
        }

        fun submitList(nlist: List<StatisticsBlock>) {
            list.run {
                clear()
                addAll(nlist)
                notifyDataSetChanged()
            }
        }
    }

    class XPAdapter : RecyclerView.Adapter<XPViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): XPViewHolder {
            val binding =
                AchievementXpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            binding.animationView.run {
                setOnClickListener {
                    if (isAnimating == false) playAnimation()
                }
            }
            return XPViewHolder(binding)
        }

        override fun onBindViewHolder(holder: XPViewHolder, position: Int) {
            holder.bind(28, 200, 60)
        }

        override fun getItemCount(): Int {
            return 1
        }
    }

    class TotalViewHolder(val binding: AchievementMediumCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            titleA: String = BaseApplication.context.getString(R.string.achievement_total_a),
            num: Int = 0,
            titleC: String = BaseApplication.context.getString(R.string.achievement_total_c),
        ) {
            binding.run {
                totalA.text = titleA
                totalB.text = num.toString()
                totalC.text = titleC
            }
        }
    }

    class StatisticsViewHolder(val binding: AchievementSmallCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StatisticsBlock) {
            binding.run {
                Glide.with(root)
                    .load(ContextCompat.getDrawable(root.context, data.drawableID))
                    .into(statisticsIcon)
                statisticsNum.text = data.num.toString()
                statisticsTitle.text = data.title
            }
        }
    }

    class StatisticsBlockSpaceItemDecoration(val listLength: Int) : RecyclerView.ItemDecoration() {
        private val space = DensityUtil.dip2px(BaseApplication.context, 12f)
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.top = space
            outRect.bottom = space
            val position = parent.getChildAdapterPosition(view)
            if (position != 0 && position < listLength + 1) {
                // Right
                if (position % 2 == 0) {
                    outRect.left = space
                } else {
                    outRect.right = space
                }
            }
        }
    }

    class XPViewHolder(val binding: AchievementXpBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(levelNow: Int, neededXP: Int, progress: Int) {
            binding.run {
                levelTitle.text =
                    binding.root.resources.getStringArray(R.array.achievement_xps).random()
                nowLevel.text = levelNow.toString()
                nextLevel.text = (levelNow + 1).toString()
                progressBar.setProgress(progress, true)
                nextLevelMessage.text = binding.root.resources.getString(
                    R.string.achievement_xp_needed,
                    neededXP.toString(),
                    (levelNow + 1).toString()
                )
            }
        }
    }

    class AchievementViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)
}