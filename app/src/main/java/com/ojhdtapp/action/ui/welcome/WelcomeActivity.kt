package com.ojhdtapp.action.ui.welcome

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import com.ojhdtapp.action.BaseApplication.Companion.context
import com.ojhdtapp.action.MainActivity
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.ActivityWelcomeBinding
import com.ojhdtapp.action.getUriToDrawable
import kotlin.properties.Delegates

class WelcomeActivity : AppCompatActivity() {
    val viewModel by viewModels<WelcomeViewModel>()
    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }
    private var isAlreadyReadAgreement by Delegates.notNull<Boolean>()
    private lateinit var imm: InputMethodManager
    private var selectedAvatarURI = getUriToDrawable(R.drawable.avatar_a)
    lateinit var binding: ActivityWelcomeBinding
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    // Handle closing an expanded recipient card when on back is pressed.
    private val closeRecipientCardOnBackPressed = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            collapseAvatarCard()
        }
    }

    // Expand & Collapse Avatar Select Card
    private fun expandAvatarCard() {
        binding.setUser.run {
            val transform = MaterialContainerTransform().apply {
                startView = this@run.userAvatar
                endView = this@run.avatarCard
                scrimColor = Color.TRANSPARENT
                endElevation = this@WelcomeActivity.resources.getDimension(
                    R.dimen.cardview_default_elevation
                )
                addTarget(this@run.avatarCard)
            }
            TransitionManager.beginDelayedTransition(this.root, transform)
            avatarCard.visibility = View.VISIBLE
        }
        closeRecipientCardOnBackPressed.isEnabled = true
    }

    private fun collapseAvatarCard() {
        binding.setUser.run {
            Glide.with(this@WelcomeActivity)
                .load(selectedAvatarURI)
                .into(userAvatar)
            val transform = MaterialContainerTransform().apply {
                startView = this@run.avatarCard
                endView = this@run.userAvatar
                scrimColor = Color.TRANSPARENT
                startElevation = this@WelcomeActivity.resources.getDimension(
                    R.dimen.cardview_default_elevation
                )
                addTarget(this@run.userAvatar)
            }
            TransitionManager.beginDelayedTransition(this.root, transform)
            avatarCard.visibility = View.GONE
        }
        closeRecipientCardOnBackPressed.isEnabled = false
    }

    // Select Avatar
    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val uri = data?.data
                uri?.let {
                    selectedAvatarURI = it
                }
                collapseAvatarCard()
//                Glide.with(context)
//                    .load(uri)
//                    .into(binding.setUser.userAvatar)
            }
        }

    private fun openAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private fun selectAvatar() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openAlbum()
        } else {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            requestPermissionLauncher.launch(permissions)
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(LayoutInflater.from(this), null, false)

//         Apply Dynamic Colors
//        if (sharedPreference.getBoolean("dynamic_color", DynamicColors.isDynamicColorAvailable())) {
//            DynamicColors.applyIfAvailable(this)
//        }

        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, closeRecipientCardOnBackPressed)

        isAlreadyReadAgreement = sharedPreference.getBoolean("isAlreadyReadAgreement", false)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // Set Dynamic Color Default value
        sharedPreference.edit()
            .putBoolean("dynamic_color", DynamicColors.isDynamicColorAvailable())
            .apply()

        // Permission
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it.containsKey(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (it[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
                        openAlbum()
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.welcome_useravatar_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else viewModel.updateState(it)
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
        binding.setUser.userAvatar.setOnClickListener {
            expandAvatarCard()
        }
        binding.setUser.run {
            // Default
            Glide.with(this@WelcomeActivity)
                .load(selectedAvatarURI)
                .into(userAvatar)
            // Selections
            Glide.with(this@WelcomeActivity)
                .load(R.drawable.avatar_a)
                .into(avatarSelectionA)
            Glide.with(this@WelcomeActivity)
                .load(R.drawable.avatar_b)
                .into(avatarSelectionB)
            Glide.with(this@WelcomeActivity)
                .load(R.drawable.avatar_c)
                .into(avatarSelectionC)
            Glide.with(this@WelcomeActivity)
                .load(R.drawable.avatar_d)
                .into(avatarSelectionD)
            Glide.with(this@WelcomeActivity)
                .load(R.drawable.avatar_e)
                .into(avatarSelectionE)
            Glide.with(this@WelcomeActivity)
                .load(R.drawable.avatar_f)
                .into(avatarSelectionF)
            Glide.with(this@WelcomeActivity)
                .load(R.drawable.avatar_g)
                .into(avatarSelectionG)

            avatarSelectionA.setOnClickListener {
                selectedAvatarURI = getUriToDrawable(R.drawable.avatar_a)
                collapseAvatarCard()
            }
            avatarSelectionB.setOnClickListener {
                selectedAvatarURI = getUriToDrawable(R.drawable.avatar_b)
                collapseAvatarCard()
            }
            avatarSelectionC.setOnClickListener {
                selectedAvatarURI = getUriToDrawable(R.drawable.avatar_c)
                collapseAvatarCard()
            }
            avatarSelectionD.setOnClickListener {
                selectedAvatarURI = getUriToDrawable(R.drawable.avatar_d)
                collapseAvatarCard()
            }
            avatarSelectionE.setOnClickListener {
                selectedAvatarURI = getUriToDrawable(R.drawable.avatar_e)
                collapseAvatarCard()
            }
            avatarSelectionF.setOnClickListener {
                selectedAvatarURI = getUriToDrawable(R.drawable.avatar_f)
                collapseAvatarCard()
            }
            avatarSelectionG.setOnClickListener {
                selectedAvatarURI = getUriToDrawable(R.drawable.avatar_g)
                collapseAvatarCard()
            }
            avatarSelectionH.setOnClickListener {
                selectAvatar()
            }
        }

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
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BODY_SENSORS,
                Manifest.permission.ACTIVITY_RECOGNITION
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
                                .putString("userAvatarURI", selectedAvatarURI.toString())
                                .putBoolean("isAlreadyDialoged", true)
                                .putBoolean("isFirstLaunch", false)
                                .apply()
                            val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onApplyThemeResource(theme: Resources.Theme?, resid: Int, first: Boolean) {
        super.onApplyThemeResource(theme, resid, first)
        when(sharedPreference.getString("theme_color", "COLOR_PRIMARY")){
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