package com.ojhdtapp.action

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ojhdtapp.action.databinding.ActivityWelcomeBinding
import com.ojhdtapp.action.ui.welcome.*

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWelcomeBinding.inflate(LayoutInflater.from(this), null, false)
        setContentView(binding.root)

        // Hide NavigationBar & StatusBar
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // ViewPager2
        binding.welcomeViewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 5

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> FirstWelcomeFragment()
                    1 -> SecondWelcomeFragment()
                    2 -> ThirdWelcomeFragment()
                    3 -> FourthWelcomeFragment()
                    else -> FifthWelcomeFragment()
                }
            }
        }
        binding.welcomeViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                var numPages = 5
                var progress = (position + positionOffset) / (numPages - 1)
                binding.root.progress = progress
                Log.d("aaa", progress.toString())
            }
        })
        binding.welcomeFAB.setOnClickListener {
            binding.welcomeViewPager.run {
                if (currentItem != 3)
                    currentItem += 1
            }
        }
    }
}