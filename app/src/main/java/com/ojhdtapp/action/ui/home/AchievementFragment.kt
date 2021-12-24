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
import com.ojhdtapp.action.logic.model.Achievement
import com.ojhdtapp.action.logic.model.StatisticsBlock

class AchievementFragment : Fragment() {
    var _binding: FragmentAchievementBinding? = null
    val binding get() = _binding!!
    val viewModel: SharedViewModel by activityViewModels()

    private var animType: Int = AnimType.NULL


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
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
            Log.d("aaa", "B"+it.toString())

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
        val achievementListAdapter = AchievementAdapters.AchievementListAdapter()
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
        }
        viewModel.allActionLive.observe(this) {
            var finishedTotalNum = 0
            it.forEach {
                it.history.forEach {
                    if (it.finished) finishedTotalNum++
                }
            }
            statisticsAdapter.setTotalNum(finishedTotalNum)
            //分类逻辑
            //分好类的StatisticsBlock列表
            val sortedStatisticsBlockList = listOf(
                StatisticsBlock(R.drawable.ic_outline_emoji_events_24),
                StatisticsBlock(R.drawable.ic_outline_emoji_events_24),
                StatisticsBlock(R.drawable.ic_outline_emoji_events_24),
            )
            statisticsAdapter.submitList(sortedStatisticsBlockList)
            mylayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position != 0 && position < sortedStatisticsBlockList.size + 1) 1
                    else 2
                }
            }
            val itemDecoration = AchievementAdapters.StatisticsBlockSpaceItemDecoration(
                sortedStatisticsBlockList.size
            )
            Log.d("aaa", "Run 1 time")
            binding.recyclerView.addItemDecoration(itemDecoration)

        }
        viewModel.gainedAchievementLive.observe(this) {
            achievementListAdapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}