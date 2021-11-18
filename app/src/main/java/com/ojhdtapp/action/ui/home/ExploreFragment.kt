package com.ojhdtapp.action.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.DeviceUtil
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentExploreBinding

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null

    val binding get() = _binding!!
    val viewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
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
            binding.toolbar2,
            findNavController(),
            appBarConfiguration
        )
        binding.appbar.apply {
            val offset = DeviceUtil.getStatusBarHeight(BaseApplication.context)
            setPadding(0, offset, 0, 0)
        }
        binding.toolbar2.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.refresh -> {
                    Log.d("aaa", "Refresh Attached")
                    viewModel.weatherRefresh()
                }
                else -> {}
            }
            false
        }

        // RecyclerView
        val weatherAdapter = ExploreAdapters.WeatherAdapter()
        val settingAdapter = ExploreAdapters.SettingAdapter()
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
            if (it.isSuccess) {
                it.getOrNull()?.apply {
                    weatherAdapter.submitList(listOf(this.weather, this.air, this.life))
                    if (this.isTemp) {
                        viewModel.weatherRefresh()
                    } else {
                        Snackbar.make(
                            binding.exploreCoordinatorLayout,
                            resources.getString(R.string.network_success),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Snackbar.make(
                    binding.exploreCoordinatorLayout,
                    resources.getString(R.string.network_error),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.settingLive.observe(this) {
            settingAdapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}