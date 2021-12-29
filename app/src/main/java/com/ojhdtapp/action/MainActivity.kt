package com.ojhdtapp.action

import android.animation.ObjectAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.transition.Fade
import com.google.android.material.color.DynamicColors
import com.ojhdtapp.action.databinding.ActivityMainBinding
import com.ojhdtapp.action.ui.home.SharedViewModel

class MainActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    val viewModel: SharedViewModel by viewModels()

    //    val viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[SharedViewModel::class.java]
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Launch WelcomeActivity if First Launch
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstLaunch = sharedPreference.getBoolean("isFirstLaunch", true)
        binding = ActivityMainBinding.inflate(layoutInflater)
        if(isFirstLaunch){
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = navHostFragment.findNavController()
        setContentView(binding.root)

        // Refresh Fragment Data
        viewModel.run {
            actionRefresh()
            suggestRefresh()
            getUserInfo()
            gainedAchievementRefresh()
            finishedActionRefresh()
            weatherRefresh(true)
        }

        // Hide NavigationBar & StatusBar
        WindowCompat.setDecorFitsSystemWindows(window, false)
//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
//            v.updatePadding(top = insets.systemWindowInsets.top)
//            insets
//        }

        //  Set SystemBar Appearance
//        val controller = binding.root.windowInsetsController
//        if (controller != null) {
//            controller.setSystemBarsAppearance(
//                APPEARANCE_LIGHT_STATUS_BARS, // value
//                APPEARANCE_LIGHT_STATUS_BARS // mask
//            )
//        }

        // Navigation
        NavigationUI.setupWithNavController(binding.homeNav, navController)
        var isFromHomeFragment = false
        var isNavigationViewShowing = true
        fun showNavigationView() {
            ObjectAnimator.ofFloat(
                binding.homeNav, "translationY",
                0f
            ).apply {
                duration = 300
                addListener(
                    doOnEnd { isNavigationViewShowing = true }
                )
                start()
            }
        }

        fun hideNavigationView() {
            ObjectAnimator.ofFloat(
                binding.homeNav, "translationY",
                DeviceUtil.getNavigationBarHeight(this).toFloat() + DensityUtil.dip2px(this, 80f)
            ).apply {
                duration = 300
                addListener(
                    doOnEnd { isNavigationViewShowing = false }
                )
                start()
            }
        }
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            arguments?.getBoolean("isHomeFragment", false)?.let { isHomeFragment ->
                if (isHomeFragment && isFromHomeFragment) {
//                    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host)
//                    val currentFragment =
//                        navHostFragment!!.childFragmentManager.fragments.firstOrNull { it.isVisible }
                    viewModel.setShouldSetTransitionLive(true)
                } else {
                    viewModel.setShouldSetTransitionLive(false)
                }
                isFromHomeFragment = isHomeFragment
                binding.homeNav.run {
                    Log.d("aaa", "motivate")
                    if (isHomeFragment && !isNavigationViewShowing) showNavigationView()
                    else if (!isHomeFragment && isNavigationViewShowing) hideNavigationView()
                }
            }
        }
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat?,
        pref: Preference?
    ): Boolean {
        pref?.let {
            when (it.key) {
                "permission" -> navController.navigate(R.id.action_settingsFragment_to_permissionsFragment)
                "account" -> navController.navigate(R.id.action_settingsFragment_to_accountFragment)
                else -> {}
            }
        }
        return true
    }
}
