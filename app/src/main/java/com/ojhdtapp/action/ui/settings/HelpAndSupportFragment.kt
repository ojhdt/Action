package com.ojhdtapp.action.ui.settings

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.transition.MaterialSharedAxis
import com.ojhdtapp.action.R
import com.ojhdtapp.action.util.BrowserUtil

class HelpAndSupportFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.help_and_support_preferences, rootKey)

        findPreference<Preference>("problem_action")?.apply {
            setOnPreferenceClickListener {
                BrowserUtil.launchURL(context, "https://guide.action.ojhdt.com/")
                true
            }
        }

        findPreference<Preference>("email")?.apply {
            setOnPreferenceClickListener {
                BrowserUtil.composeEmail(
                    context,
                    arrayOf("ojhdtmail@gmail.com"),
                    getString(R.string.email_subject)
                )
                true
            }
        }

        findPreference<Preference>("tg")?.apply {
            setOnPreferenceClickListener {
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
//            it.inflateMenu(R.menu.settings_toolbar)
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
    }
}