package com.ojhdtapp.action.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import com.ojhdtapp.action.*
import com.ojhdtapp.action.databinding.FragmentAchievementBinding
import com.ojhdtapp.action.databinding.FragmentActionBinding
import com.ojhdtapp.action.logic.detector.AchievementPusher
import com.ojhdtapp.action.logic.model.Achievement
import com.ojhdtapp.action.logic.model.StatisticsBlock

class AchievementFragment : Fragment() {
    var _binding: FragmentAchievementBinding? = null
    val binding get() = _binding!!
    val viewModel: SharedViewModel by activityViewModels()
    val achievementPusher = AchievementPusher.getPusher()

    private var animType: Int = AnimType.NULL

    private var isSortByTime = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            animType = it.getInt("ANIM_TYPE", 4)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAchievementBinding.inflate(inflater, container, false)
        // Set default Transition
        viewModel.shouldSetTransitionLive.observeOnce(this) {

            animType = if (it) AnimType.FADE else AnimType.NULL
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup Appbar
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.actionFragment,
                R.id.achievementFragment,
                R.id.exploreFragment
            )
        )
        NavigationUI.setupWithNavController(
            binding.toolbar3,
            findNavController(),
            appBarConfiguration
        )
//        binding.appbar.apply {
//            val offset = DeviceUtil.getStatusBarHeight(BaseApplication.context)
//            setPadding(0, offset, 0, 0)
//        }
        binding.toolbar3.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.refresh -> viewModel.finishedActionRefresh()
                else -> {}
            }
            false
        }

        // Set AnimType if Necessary
        when (animType) {
            AnimType.FADE -> {
                exitTransition = Fade().apply {
                    duration =
                        resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
                }
                reenterTransition = Fade().apply {
                    duration =
                        resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
                }
            }
            AnimType.HOLD -> {
                exitTransition = Hold()
                reenterTransition = Hold()
            }
            AnimType.SHARED_AXIS_Z -> {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                    duration =
                        resources.getInteger(R.integer.material_motion_duration_long_1)
                            .toLong()
                }
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                    duration =
                        resources.getInteger(R.integer.material_motion_duration_long_1)
                            .toLong()
                }
            }
            AnimType.ELEVATIONSCALE -> {
                exitTransition = MaterialElevationScale(false).apply {
                    duration =
                        resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
                }
                reenterTransition = MaterialElevationScale(true).apply {
                    duration =
                        resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
                }
            }
            else -> {}
        }

        val statisticsAdapter = AchievementAdapters.StatisticsAdapter()
        val xpAdapter = AchievementAdapters.XPAdapter()
        val achievementListAdapter = AchievementAdapters.AchievementListAdapter(object :
            AchievementAdapters.SwitchSortByListener {
            override fun onClick() {
                isSortByTime = !isSortByTime
                viewModel.gainedAchievementRefresh()
            }
        })
        val mylayoutManager = GridLayoutManager(context, 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return 2
                }
            }
        }
        binding.recyclerView.run {
            val concatAdapter = ConcatAdapter(statisticsAdapter, xpAdapter, achievementListAdapter)
            adapter = concatAdapter
            layoutManager = mylayoutManager
            while (itemDecorationCount != 0) {
                removeItemDecorationAt(0)
            }
        }
        viewModel.allActionLive.observeOnce(this) { it ->
            var totalFinished = 0
            var totalSaveWater = 0f
            var totalSaveElectricity = 0f
            var totalSaveTree = 0f
            it.forEach { action ->
                action.history.forEach {
                    if (it.finished) {
                        totalSaveWater += action.canSaveWater
                        totalSaveElectricity += action.canSaveElectricity
                        totalSaveTree += action.canSaveTree
                        totalFinished++
                    }
                }
            }
            statisticsAdapter.setTotalNum(totalFinished)
            //分好类的StatisticsBlock列表
            val sortedStatisticsBlockList = listOf(
                StatisticsBlock(
                    R.drawable.ic_outline_bolt_24,
                    getString(R.string.achievement_electricity),
                    String.format("%.3f", totalSaveElectricity),
                    getString(R.string.achievement_electricity_unit)
                ),
                StatisticsBlock(
                    R.drawable.ic_outline_water_drop_24,
                    getString(R.string.achievement_water), String.format("%.3f", totalSaveWater),
                    getString(R.string.achievement_water_unit)
                ),
                StatisticsBlock(
                    R.drawable.ic_outline_forest_24,
                    getString(R.string.achievement_tree), String.format("%.3f", totalSaveTree),
                    getString(R.string.achievement_tree_unit)
                ),
            )
            statisticsAdapter.submitList(mutableListOf(StatisticsBlock()).apply {
                addAll(sortedStatisticsBlockList)
            })
            mylayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position != 0 && position < sortedStatisticsBlockList.size + 1) 1
                    else 2
                }
            }
            val itemDecoration = AchievementAdapters.StatisticsBlockSpaceItemDecoration(
                sortedStatisticsBlockList.size
            )
            binding.recyclerView.addItemDecoration(itemDecoration)
        }

        val expInformation = achievementPusher.getExpInformation()
        xpAdapter.submitValue(
            expInformation.levelNow,
            expInformation.neededExp,
            expInformation.progress
        )

        fun updateAchievement(data: List<Achievement>, isSortByTime: Boolean) {
            val list =
                mutableListOf<Achievement>(
                    if (isSortByTime) Achievement(
                        drawableID = 0,
                        title = System.currentTimeMillis().toString()
                    ) else Achievement()
                )
            list.apply {
                addAll(if (isSortByTime) data.sortedBy { it.time.time } else data.sortedBy { it.title })
            }
            achievementListAdapter.submitList(list)
        }
        viewModel.gainedAchievementLive.observe(viewLifecycleOwner) {
            updateAchievement(it, isSortByTime)
        }

//        viewModel.isSortByTimeLive.observe(viewLifecycleOwner) {
//            updateAchievement(viewModel.gainedAchievementLive.value!!, it)
//        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}