package com.ojhdtapp.action.ui.settings

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.amap.api.maps.AMap
import com.amap.api.maps.TextureMapView
import com.amap.api.maps.model.MyLocationStyle
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.transition.MaterialSharedAxis
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentMapBinding
import com.ojhdtapp.action.isDarkTheme

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var amap: AMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup Transition
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup Appbar
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar?.let {
//            it.inflateMenu()
            NavigationUI.setupWithNavController(
                it,
                navController,
                appBarConfiguration
            )
            it.setOnMenuItemClickListener {
                when (it.itemId) {
                    else -> {}
                }
                false
            }
        }

        // AMap
        binding.textureMapView.onCreate(savedInstanceState)
        amap = binding.textureMapView.map
        amap.uiSettings.run {
            isMyLocationButtonEnabled = false
            isZoomControlsEnabled = false
        }
        if (isDarkTheme()) {
            amap.mapType = AMap.MAP_TYPE_NIGHT
        }

        // Setup FAB
        binding.myLocation.setOnClickListener {
            setMyLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.textureMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.textureMapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.textureMapView.onDestroy()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.textureMapView.onSaveInstanceState(outState)
    }

    // Functions
    private fun setMyLocation() {
        Log.d("aaa", "locate")
        amap.myLocationStyle = MyLocationStyle().apply {
            myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
//            myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
            interval(2000)
            showMyLocation(true)
        }
        amap.isMyLocationEnabled = true
    }
}