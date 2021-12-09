package com.ojhdtapp.action.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.ojhdtapp.action.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}