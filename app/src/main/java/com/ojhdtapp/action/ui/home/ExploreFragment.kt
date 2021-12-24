package com.ojhdtapp.action.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.Fade
import com.google.android.material.snackbar.Snackbar
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.DeviceUtil
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentExploreBinding

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null

    val binding get() = _binding!!
    val viewModel: SharedViewModel by activityViewModels()
    private lateinit var _navDestinationChangedListener: NavController.OnDestinationChangedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        // Restore the Default Transition when Navigating Home Fragment
        _navDestinationChangedListener =
            NavController.OnDestinationChangedListener { navController: NavController, navDestination: NavDestination, bundle: Bundle? ->
                Log.d("aaa", "One trigger")
//            Log.d("aaa",navController.previousBackStackEntry?.destination?.displayName.toString())
                bundle?.getBoolean("isHomeFragment", false)?.let {
                    if (it) {
                        exitTransition = Fade()
                        reenterTransition = Fade()
                    }
                }
            }
        findNavController().addOnDestinationChangedListener(_navDestinationChangedListener)
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
//        binding.appbar.apply {
//            val offset = DeviceUtil.getStatusBarHeight(BaseApplication.context)
//            setPadding(0, offset, 0, 0)
//        }
        binding.toolbar2.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.refresh -> {
                    viewModel.weatherRefresh()
                }
                else -> {}
            }
            false
        }

        // Restore the Default Transition when Navigating Home Fragments
        findNavController().addOnDestinationChangedListener { navController: NavController, navDestination: NavDestination, bundle: Bundle? ->
            bundle?.getBoolean("isHomeFragment", false)?.let {
                if (it) {
                    exitTransition = Fade()
                    reenterTransition = Fade()
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
            if (it.isSuccess) {
                it.getOrNull()?.apply {
                    val list = listOf(
                        this.weather, this.air, this.life,
                        Pair(
                            R.drawable.ic_outline_settings_24,
                            BaseApplication.context.getString(R.string.setting)
                        ),
                        Pair(
                            R.drawable.ic_outline_settings_24,
                            BaseApplication.context.getString(R.string.setting)
                        ),
                        Pair(
                            R.drawable.ic_outline_settings_24,
                            BaseApplication.context.getString(R.string.setting)
                        ),
                        Pair(
                            R.drawable.ic_outline_settings_24,
                            BaseApplication.context.getString(R.string.setting)
                        ),
                        Pair(
                            R.drawable.ic_outline_settings_24,
                            BaseApplication.context.getString(R.string.setting)
                        )
                    )
                    weatherAdapter.submitList(list)
                    if (this.isTemp) {
                        viewModel.weatherRefresh()
                    } else {
//                        Snackbar.make(
//                            binding.exploreCoordinatorLayout,
//                            resources.getString(R.string.network_success),
//                            Snackbar.LENGTH_SHORT
//                        ).show()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        findNavController().removeOnDestinationChangedListener(_navDestinationChangedListener)
        _binding = null
    }
}