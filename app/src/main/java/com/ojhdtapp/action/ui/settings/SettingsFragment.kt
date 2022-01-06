package com.ojhdtapp.action.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.color.DynamicColors
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.ojhdtapp.action.R
import rikka.preference.SimpleMenuPreference

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>("action_expired_time")?.apply {
            setOnPreferenceClickListener {
                false
            }
        }

        findPreference<SimpleMenuPreference>("dark_mode")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                val oldValue = sharedPreferences.getString("dark_mode", "MODE_NIGHT_FOLLOW_SYSTEM")
                if (oldValue != newValue) {
                    setDefaultNightMode(
                        when (newValue) {
                            "MODE_NIGHT_NO" -> MODE_NIGHT_NO
                            "MODE_NIGHT_YES" -> MODE_NIGHT_YES
                            "MODE_NIGHT_FOLLOW_SYSTEM" -> MODE_NIGHT_FOLLOW_SYSTEM
                            else -> -2
                        }
                    )
                }
                true
            }
        }

        findPreference<SwitchPreferenceCompat>("dynamic_color")?.apply {
            if (!DynamicColors.isDynamicColorAvailable()) {
                isEnabled = false
            } else {
                setOnPreferenceClickListener {
                    activity?.recreate()
                    true
                }
            }
        }
    }

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup Appbar
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar?.let {
            it.inflateMenu(R.menu.settings_toolbar)
            NavigationUI.setupWithNavController(
                it,
                navController,
                appBarConfiguration
            )
            it.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.reset -> {}
                    else -> {}
                }
                false
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
}