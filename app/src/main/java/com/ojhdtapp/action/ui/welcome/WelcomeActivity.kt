package com.ojhdtapp.action.ui.welcome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.ActivityWelcomeBinding

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
                var numProgress = (position + positionOffset)
                binding.root.run {
                    if (numProgress >= 0 && numProgress < 1) {
                        setTransition(R.id.page1, R.id.page2)
                        progress = numProgress % 1
                    } else if (numProgress >= 1 && numProgress < 2) {
                        setTransition(R.id.page2, R.id.page3)
                        progress = numProgress % 1
                    } else if (numProgress >= 2 && numProgress < 3) {
                        setTransition(R.id.page3, R.id.page4)
                        progress = numProgress % 1
                    } else {
                        setTransition(R.id.page4, R.id.page5)
                        progress = numProgress % 1
                    }
//                var numProgress = (position + positionOffset) / (numPages - 1) * numPages
//                binding.root.progress = progress
                }
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