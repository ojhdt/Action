package com.ojhdtapp.action.ui.settings

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdate
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentMapBinding
import com.ojhdtapp.action.isDarkTheme
import com.ojhdtapp.action.util.LocationUtil

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var amap: AMap
    private var myLocationMarker: Marker? = null

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
        amap.isMyLocationEnabled = false
        amap.uiSettings.run {
            isMyLocationButtonEnabled = false
            isZoomControlsEnabled = false
            isCompassEnabled = true
        }
        if (isDarkTheme()) {
            amap.mapType = AMap.MAP_TYPE_NIGHT
        }
        getMyLocation()
        drawMyLocationMarker()

        // Setup FAB
        binding.myLocation.setOnClickListener {
            getMyLocation()
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
        myLocationMarker?.destroy()
        myLocationMarker = null
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.textureMapView.onSaveInstanceState(outState)
    }

    // Functions
    private fun getMyLocation() {
//        amap.myLocationStyle = MyLocationStyle().apply {
//            myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
//            interval(2000)
//            showMyLocation(true)
//        }
        if (ContextCompat.checkSelfPermission(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            moveToMyLocation()
        } else {
            Snackbar.make(
                binding.mapCoordinatorLayout,
                R.string.location_permission_denied,
                Snackbar.LENGTH_SHORT
            )
                .setAction(R.string.go_to_authorization) {
                    findNavController().navigate(R.id.action_global_permissionsFragment)
                }
                .show()
        }
    }

    private fun moveToMyLocation() {
        val location = LocationUtil.getLocation()
        location?.let {
            val lating = LatLng(it.latitude, it.longitude)
            val mCaremaUpdate = CameraUpdateFactory.newLatLngZoom(lating, 17f)
            amap.run {
                animateCamera(mCaremaUpdate,500,object:AMap.CancelableCallback{
                    override fun onFinish() {
                    }

                    override fun onCancel() {
                    }
                })
            }
        }
    }

    private fun drawMyLocationMarker(){
        val location = LocationUtil.getLocation()
        location?.let {
            val options = MarkerOptions()
            options.position(LatLng(location.latitude, location.longitude))
//            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_beenhere_24))
            myLocationMarker = amap.addMarker(options)
        }
    }
}