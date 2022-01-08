package com.ojhdtapp.action.ui.home

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.Fade
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import com.ojhdtapp.action.*
import com.ojhdtapp.action.databinding.FragmentExploreBinding
import com.ojhdtapp.action.logic.model.LifeMessageBlock
import com.ojhdtapp.action.logic.model.WeatherBlock
import com.ojhdtapp.action.logic.model.WeatherMessageBlock
import com.ojhdtapp.action.ui.dialog.VersionDialogFragment
import java.util.jar.Manifest

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null

    val binding get() = _binding!!
    val viewModel: SharedViewModel by activityViewModels()

    private var animType: Int = AnimType.NULL


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            animType = it.getInt("ANIM_TYPE", 4)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        // Set default Transition
        viewModel.shouldSetTransitionLive.observeOnce(this) {
            animType = if (it) AnimType.FADE else AnimType.NULL
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Postpone Transition
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        // Setup Appbar
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.actionFragment,
                R.id.achievementFragment,
                R.id.exploreFragment
            )
        )
        NavigationUI.setupWithNavController(
            binding.toolbar2,
            findNavController(),
            appBarConfiguration
        )
//        binding.appbar.apply {
//            val offset = DeviceUtil.getStatusBarHeight(BaseApplication.context)
//            setPadding(0, offset, 0, 0)
//        }

        binding.toolbar2.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.refresh -> {
                    refreshWeather(true)
                }
                R.id.version -> {
                    showVersionDialog()
                }
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
        }

        // RecyclerView
        val weatherAdapter = ExploreAdapters.WeatherAdapter()
        binding.recyclerView.run {
            adapter = ConcatAdapter(weatherAdapter)
            layoutManager = GridLayoutManager(context, 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (position) {
                            1 -> 1
                            2 -> 1
                            else -> 2
                        }
                    }
                }
            }
            addItemDecoration(ExploreAdapters.WeatherMessageBlockSpaceItemDecoration())
        }
        viewModel.weatherLive.observe(this) {
            val result = it.getOrNull()
            val list = listOf(
                if (it.isSuccess) result?.weather else
                    WeatherBlock(
                        getString(R.string.loading_location_failed),
                        getString(R.string.loading_data_failed),
                        WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
                        WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
                        WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
                        WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
                        WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
                        WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0),
                        WeatherBlock.WeatherTemperature(R.raw.weather_sunny, 0)
                    ),
                if (it.isSuccess) result?.air else
                    WeatherMessageBlock(
                        R.drawable.ic_outline_air_24,
                        BaseApplication.context.getString(R.string.air),
                        0,
                        0
                    ),
                if (it.isSuccess) result?.life else
                    LifeMessageBlock(0, 0, 0, 0),
                ExploreAdapters.SettingAccentData(
                    R.drawable.ic_outline_directions_run_24,
                    getString(R.string.explore_action),
                    getString(R.string.explore_action_description),
                    object : MyOnClickListener {
                        override fun onClick() {
                            exitTransition =
                                MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                                    duration =
                                        resources.getInteger(R.integer.material_motion_duration_long_1)
                                            .toLong()
                                }
                            reenterTransition =
                                MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                                    duration =
                                        resources.getInteger(R.integer.material_motion_duration_long_1)
                                            .toLong()
                                }
                            findNavController().navigate(R.id.action_exploreFragment_to_actionArchiveFragment)
                        }
                    }
                ),
                ExploreAdapters.SettingAccentData(
                    R.drawable.ic_outline_article_24,
                    getString(R.string.explore_suggest),
                    getString(R.string.explore_suggest_description),
                    object : MyOnClickListener {
                        override fun onClick() {
                            exitTransition =
                                MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                                    duration =
                                        resources.getInteger(R.integer.material_motion_duration_long_1)
                                            .toLong()
                                }
                            reenterTransition =
                                MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                                    duration =
                                        resources.getInteger(R.integer.material_motion_duration_long_1)
                                            .toLong()
                                }
                            findNavController().navigate(R.id.action_exploreFragment_to_suggestArchiveFragment)
                        }
                    }
                ),
                ExploreAdapters.SettingData(R.drawable.ic_outline_settings_24,
                    BaseApplication.context.getString(R.string.setting),
                    object : MyOnClickListener {
                        override fun onClick() {
                            findNavController().navigate(R.id.action_exploreFragment_to_settingsFragment)
                        }
                    }),
                ExploreAdapters.SettingData(R.drawable.ic_outline_help_outline_24,
                    BaseApplication.context.getString(R.string.help_and_support),
                    object : MyOnClickListener {
                        override fun onClick() {
                            findNavController().navigate(R.id.action_exploreFragment_to_helpAndSupportFragment)
                        }
                    }),
                ExploreAdapters.SettingData(R.drawable.ic_outline_thumb_up_24,
                    BaseApplication.context.getString(R.string.vote),
                    object : MyOnClickListener {
                        override fun onClick() {
                            findNavController().navigate(R.id.action_exploreFragment_to_settingsFragment)
                        }
                    }),
                ExploreAdapters.SettingData(R.drawable.ic_outline_info_24,
                    BaseApplication.context.getString(R.string.about),
                    object : MyOnClickListener {
                        override fun onClick() {
                            findNavController().navigate(R.id.action_exploreFragment_to_aboutFragment)
                        }
                    }),
            )
            weatherAdapter.submitList(list)
            if (result?.isTemp == true) {
                refreshWeather()
            }

            if (it.isFailure) {
                Snackbar.make(
                    binding.exploreCoordinatorLayout,
                    resources.getString(R.string.network_error),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Functions

    private fun showVersionDialog() {
        val newFragment = VersionDialogFragment()
        newFragment.show(parentFragmentManager, "version")
    }

    private fun refreshWeather(createTemp:Boolean = false) {
        if (ContextCompat.checkSelfPermission(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.weatherRefresh(createTemp)
        } else {
            Snackbar.make(
                binding.exploreCoordinatorLayout,
                R.string.location_permission_denied,
                Snackbar.LENGTH_SHORT
            )
                .setAction(R.string.go_to_authorization) {
                    findNavController().navigate(R.id.action_global_permissionsFragment)
                }
                .show()
        }
    }
}