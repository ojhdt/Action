package com.ojhdtapp.action.ui.settings

import android.content.Intent
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
import com.ojhdtapp.action.ui.welcome.WelcomeActivity
import rikka.preference.SimpleMenuPreference
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>("action_expired_time")?.apply {

        }

        findPreference<SimpleMenuPreference>("language")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue != value) {
                    val locale = if (newValue == "SYSTEM") Locale.getDefault() else {
                        Locale.forLanguageTag(newValue as String)
                    }
                    resources.configuration.setLocale(locale)
                    activity?.recreate()
                }
                true
            }

            val userLocale = resources.configuration.locales[0]
            entries = arrayListOf(entries[0].toString()).apply {
                addAll(getAppLanguages().map {
                    getString(
                        R.string.hyphen_messages,
                        Locale.forLanguageTag(it).getDisplayName(Locale.forLanguageTag(it)),
                        Locale.forLanguageTag(it).getDisplayName(userLocale)
                    )
                })
            }.toTypedArray()
            entryValues = arrayListOf(entryValues[0].toString()).apply {
                addAll(getAppLanguages())
            }.toTypedArray()
//            summary = if(value == "SYSTEM") getString(R.string.language_follow_system) else{
//                getString(R.string.hyphen_messages,
//                    Locale.forLanguageTag(value).getDisplayName(Locale.forLanguageTag(value)),
//                    Locale.forLanguageTag(value).getDisplayName(userLocale))
//            }
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

            } else {
                setOnPreferenceChangeListener { preference, newValue ->
                    summary = getString(R.string.dynamic_color_summary_edited)
                    true
                }
            }
        }

        findPreference<SimpleMenuPreference>("theme_color")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue != value) {
                    activity?.recreate()
                }
                true
            }
        }

        findPreference<Preference>("guide")?.apply {
            setOnPreferenceClickListener {
                val intent = Intent(activity, WelcomeActivity::class.java)
                startActivity(intent)
                true
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

    // Function
    private fun getAppLanguages(): List<String> {
        val configuration = resources.configuration
        val originalLocale = configuration.locales[0]
        val langList = mutableListOf<String>(Locale.ENGLISH.language)
        context?.assets?.locales?.forEach {
            if (!langList.contains(it))
                langList.add(it)
        }
        return langList
    }
}