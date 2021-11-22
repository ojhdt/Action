package com.ojhdtapp.action

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.ojhdtapp.action.databinding.ActivityMainBinding
import com.ojhdtapp.action.ui.home.SharedViewModel

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    val viewModel:SharedViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.actionContentFragment ->{
                    binding.homeNav.visibility = View.GONE
                }
                R.id.suggestContentFragment -> {
                    binding.homeNav.visibility = View.GONE
                }
                else -> {
                    binding.homeNav.visibility = View.VISIBLE
                }
            }
        }


    }
}