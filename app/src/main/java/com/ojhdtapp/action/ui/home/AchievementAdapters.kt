package com.ojhdtapp.action.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.DrawableUtils
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.AchievementCardContainerBinding
import com.ojhdtapp.action.databinding.AchievementMediumCardBinding
import com.ojhdtapp.action.databinding.AchievementSmallCardBinding
import com.ojhdtapp.action.logic.model.StatisticsBlock

class StatisticsAdapter(itemView: View) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var totalTitleA: String = BaseApplication.context.getString(R.string.action_title_a)
    private var totalNum: Int = 0
    private var totalTitleC: String = BaseApplication.context.getString(R.string.action_title_c)
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
            1 -> {
                val binding = AchievementCardContainerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return object : RecyclerView.ViewHolder(binding.root) {}
            }
            else -> {
                val binding = AchievementSmallCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent.findViewById(R.id.achievementContainer),
                    true
                )
                return StatisticsViewHolder(binding)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.bindingAdapterPosition) {
            0 -> {
                (holder as TotalViewHolder).bind(totalTitleA, totalNum, totalTitleC)
            }
            1 -> {
            }
            else -> {
                val item = list[position - 2]
                (holder as StatisticsViewHolder).bind(item)
            }
        }
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 0 else 1
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