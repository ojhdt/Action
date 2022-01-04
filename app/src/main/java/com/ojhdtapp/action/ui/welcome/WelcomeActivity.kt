package com.ojhdtapp.action.ui.welcome

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ojhdtapp.action.BaseApplication.Companion.context
import com.ojhdtapp.action.MainActivity
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.ActivityWelcomeBinding
import kotlin.properties.Delegates

class WelcomeActivity : AppCompatActivity() {
    val viewModel by viewModels<WelcomeViewModel>()
    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }
    private var isAlreadyReadAgreement by Delegates.notNull<Boolean>()
    private lateinit var imm: InputMethodManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWelcomeBinding.inflate(LayoutInflater.from(this), null, false)
        setContentView(binding.root)

        isAlreadyReadAgreement = sharedPreference.getBoolean("isAlreadyReadAgreement", false)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // Permission
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                viewModel.updateState(it)
            }

        // Hide NavigationBar & StatusBar
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Hide KeyBoard
        fun hideKeyBoard() {
            if (imm.isActive) {
                imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
            }
        }

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
            var numProgress = 0f
            val numPages = 5
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                numProgress = (position + positionOffset)
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
                    } else if (numProgress >= 3 && numProgress < 4) {
                        setTransition(R.id.page4, R.id.page5)
                        progress = numProgress % 1
                    } else {
                        if (!binding.done.lottieAnimationView.isAnimating) binding.done.lottieAnimationView.playAnimation()
                        setTransition(R.id.page4, R.id.page5)
                        progress = 1f
                    }
                    if (numProgress <= 0 || numProgress >= 2) hideKeyBoard()
//                var numProgress = (position + positionOffset) / (numPages - 1) * numPages
//                binding.root.progress = progress
                }
            }
        })

        // Avatars
        Glide.with(this)
            .load

        // Permission RV
        val myAdapter = PermissionAdapter()
        binding.setPermission.recyclerView2.run {
            layoutManager = LinearLayoutManager(context)
            adapter = myAdapter
        }
        viewModel.permissionLive.observe(this) {
            myAdapter.submitList(it)

        }
        binding.setPermission.materialButton.setOnClickListener {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BODY_SENSORS
            )
            requestPermissionLauncher.launch(permissions)
        }

        // Permission Btn
        viewModel.permissionStateLive.observe(this) {
            if (it.values.contains(false)) {
                binding.setPermission.materialButton.run {
                    isEnabled = true
                    text = getString(R.string.welcome_permission_btn)
                }
            } else {
                binding.setPermission.materialButton.run {
                    isEnabled = false
                    text = getString(R.string.welcome_permission_btn_disabled)
                }
            }
        }

        // Agreement CheckBox
        binding.agreement.materialCheckBox.isChecked = isAlreadyReadAgreement
        binding.agreement.materialCheckBox.setOnCheckedChangeListener { _, isChecked ->
            isAlreadyReadAgreement = isChecked
        }

        // Lottie
        binding.done.lottieAnimationView.run {
            setOnClickListener {
                if (!isAnimating) playAnimation()
            }
        }

        // Show Dialog
        fun showEditTextErrorDialog() {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.welcome_username_error_title)
                .setMessage(R.string.welcome_username_error)
                .setPositiveButton(R.string.welcome_username_error_positive) { dialogInterface: DialogInterface, i: Int ->
                    binding.welcomeViewPager.currentItem++
                }
                .setNegativeButton(R.string.welcome_username_error_negative) { dialogInterface: DialogInterface, i: Int ->
                }
                .show()
        }

        fun showPermissionNotGrantedWarningDialog() {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.welcome_permission_not_granted_title)
                .setMessage(R.string.welcome_permission_not_granted)
                .setPositiveButton(R.string.welcome_permission_not_granted_positive) { dialogInterface: DialogInterface, i: Int ->
                    sharedPreference.edit()
                        .putBoolean("isAlreadyDialoged", true)
                        .apply()
                }
                .setNegativeButton(R.string.welcome_permission_not_granted_negative) { dialogInterface: DialogInterface, i: Int -> }
                .show()
        }

        fun showHasErrorDialog() {
            val message = getString(
                R.string.welcome_done_error,
                if (binding.setUser.usernameEditText.text.toString().length > 10) getString(R.string.welcome_done_error_a) else "",
                if (!isAlreadyReadAgreement) getString(R.string.welcome_done_error_b) else "",
                if (viewModel.permissionStateLive.value?.containsValue(false) != false) getString(R.string.welcome_done_error_c) else ""
            )
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.welcome_done_error_title)
                .setMessage(message)
                .setPositiveButton(R.string.welcome_done_error_positive) { dialogInterface: DialogInterface, i: Int ->
                }
                .show()
        }

        // FAB
        binding.welcomeFAB.setOnClickListener {
            binding.welcomeViewPager.run {
                when (currentItem) {
                    0 -> currentItem++
                    1 -> {
                        if (binding.setUser.usernameEditText.text.toString().length > 10) {
                            showEditTextErrorDialog()
                        } else currentItem++
                    }
                    2 -> {
                        val isAlreadyDialoged =
                            sharedPreference.getBoolean("isAlreadyDialoged", false)
                        if (!isAlreadyDialoged && viewModel.permissionStateLive.value?.containsValue(
                                false
                            ) != false
                        ) {
                            showPermissionNotGrantedWarningDialog()
                        } else currentItem++
                    }
                    3 -> {
                        if (isAlreadyReadAgreement) {
                            currentItem++
                        } else {
                            Toast.makeText(
                                this@WelcomeActivity,
                                getString(R.string.welcome_agreement_toast),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    4 -> {
                        if (!isAlreadyReadAgreement) {
                            showHasErrorDialog()
                        } else {
                            val username =
                                if (binding.setUser.usernameEditText.text.isNullOrBlank())
                                    getString(R.string.default_username)
                                else if (binding.setUser.usernameEditText.text.toString().length > 10) getString(
                                    R.string.default_username
                                )
                                else binding.setUser.usernameEditText.text
                            sharedPreference.edit()
                                .putString("username", username.toString())
                                .putBoolean("isAlreadyDialoged", true)
                                .putBoolean("isFirstLaunch", false)
                                .apply()
                            val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}