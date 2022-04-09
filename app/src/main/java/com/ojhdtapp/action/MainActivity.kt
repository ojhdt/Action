package com.ojhdtapp.action

import android.animation.ObjectAnimator
import android.content.*
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.view.WindowCompat
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.color.DynamicColors
import com.ojhdtapp.action.databinding.ActivityMainBinding
import com.ojhdtapp.action.logic.detector.AchievementPusher
import com.ojhdtapp.action.logic.detector.DetectService
import com.ojhdtapp.action.ui.home.SharedViewModel
import com.ojhdtapp.action.ui.welcome.WelcomeActivity
import com.ojhdtapp.action.util.DensityUtil
import com.ojhdtapp.action.util.DeviceUtil

class MainActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    val viewModel: SharedViewModel by viewModels()
    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    //    val viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[SharedViewModel::class.java]
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        Log.d("aaa", "water" + R.drawable.ic_outline_water_drop_24.toString())
        Log.d("aaa", "ele" + R.drawable.ic_outline_bolt_24.toString())
        Log.d("aaa", "tree" + R.drawable.ic_outline_forest_24.toString())

        // Apply Dynamic Colors
//        if (sharedPreference.getBoolean("dynamic_color", DynamicColors.isDynamicColorAvailable())) {
//            DynamicColors.applyIfAvailable(this)
//        }

        // Launch WelcomeActivity if First Launch
        val isFirstLaunch = sharedPreference.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
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
                    if (isHomeFragment && !isNavigationViewShowing) showNavigationView()
                    else if (!isHomeFragment && isNavigationViewShowing) hideNavigationView()
                }
            }
        }

        // Bind _FenceService
//        val fenceServiceIntent = Intent(this, _FenceService::class.java)
//        startService(fenceServiceIntent)
//        bindService(fenceServiceIntent, object : ServiceConnection {
//            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//                val binder = service as _FenceService.FenceBinder
//            }
//
//            override fun onServiceDisconnected(name: ComponentName?) {
//            }
//        }, Context.BIND_AUTO_CREATE)

        // Bind _TransitionService
//        val transitionServiceIntent = Intent(this, _TransitionService::class.java)
//        startService(transitionServiceIntent)
//        bindService(transitionServiceIntent, object : ServiceConnection {
//            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            }
//
//            override fun onServiceDisconnected(name: ComponentName?) {
//            }
//        }, Context.BIND_AUTO_CREATE)

        // Bind DetectService
        val detectServiceIntent = Intent(this, DetectService::class.java)
        startService(detectServiceIntent)
        bindService(detectServiceIntent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as DetectService.DetectBinder
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }, Context.BIND_AUTO_CREATE)

        // Check Achievement
        AchievementPusher.getPusher().tryPushingNewAchievement()
    }

//    override fun onPreferenceStartFragment(
//        caller: PreferenceFragmentCompat?,
//        pref: Preference?
//    ): Boolean {
//        pref?.let {
//            when (it.key) {
//                "permission" -> navController.navigate(R.id.action_settingsFragment_to_permissionsFragment)
//                "account" -> navController.navigate(R.id.action_settingsFragment_to_accountFragment)
//                "state" -> navController.navigate(R.id.action_settingsFragment_to_stateFragment)
//                "set_locate" -> navController.navigate(R.id.action_settingsFragment_to_mapFragment)
//                else -> {}
//            }
//        }
//        return true
//    }

    override fun onApplyThemeResource(theme: Resources.Theme?, resid: Int, first: Boolean) {
        super.onApplyThemeResource(theme, resid, first)
        if (!sharedPreference.getBoolean(
                "dynamic_color",
                DynamicColors.isDynamicColorAvailable()
            )
        ) {
            when (sharedPreference.getString("theme_color", "COLOR_PRIMARY")) {
                "SAKURA" -> theme?.applyStyle(R.style.Sakura, true)
                "ROSE" -> theme?.applyStyle(R.style.Rose, true)
                "TEA" -> theme?.applyStyle(R.style.Tea, true)
                "GARDENIA" -> theme?.applyStyle(R.style.Gardenia, true)
                "WATER" -> theme?.applyStyle(R.style.Water, true)
                "FUJIMURASAKI" -> theme?.applyStyle(R.style.Fujimurasaki, true)
                "RURI" -> theme?.applyStyle(R.style.Ruri, true)
                else -> {}
            }
        }
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        pref.let {
            when (it.key) {
                "permission" -> navController.navigate(R.id.action_settingsFragment_to_permissionsFragment)
                "account" -> navController.navigate(R.id.action_settingsFragment_to_accountFragment)
                "state" -> navController.navigate(R.id.action_settingsFragment_to_stateFragment)
                "set_locate" -> navController.navigate(R.id.action_settingsFragment_to_mapFragment)
                else -> {}
            }
        }
        return true
    }
}
