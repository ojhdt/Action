package com.ojhdtapp.action.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentAchievementBinding
import com.ojhdtapp.action.databinding.FragmentActionBinding
import com.ojhdtapp.action.logic.model.StatisticsBlock

class AchievementFragment : Fragment() {
    var _binding: FragmentAchievementBinding? = null

    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAchievementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.actionFragment,
                R.id.achievementFragment,
                R.id.exploreFragment
            )
        )
        NavigationUI.setupWithNavController(
            binding.toolbar,
            findNavController(),
            appBarConfiguration
        )

        val statisticsAdapter = StatisticsAdapter()
        binding.recyclerView.run { 
            adapter = statisticsAdapter
            layoutManager = GridLayoutManager(context, 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position != 0 && position < 4 + 1) 1
                        else 2
                    }
                }
            }
        }
        statisticsAdapter.setTotalNum(30)
        statisticsAdapter.submitList(
            listOf(
                StatisticsBlock(R.drawable.ic_outline_emoji_events_24),
                StatisticsBlock(R.drawable.ic_outline_emoji_events_24),
                StatisticsBlock(R.drawable.ic_outline_emoji_events_24),
                StatisticsBlock(R.drawable.ic_outline_emoji_events_24),
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}