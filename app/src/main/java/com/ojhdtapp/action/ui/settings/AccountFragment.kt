package com.ojhdtapp.action.ui.settings

import android.content.DialogInterface
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import com.ojhdtapp.action.R
import com.ojhdtapp.action.getUriToDrawable
import com.ojhdtapp.action.logic.AppDataBase
import com.ojhdtapp.action.ui.dialog.AvatarPickerDialogFragment
import com.ojhdtapp.action.ui.dialog.VersionDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AccountFragment : PreferenceFragmentCompat() {
    private val sharedPreference: SharedPreferences? by lazy {
        context?.let {
            PreferenceManager.getDefaultSharedPreferences(it)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.account_preferences, rootKey)

        findPreference<EditTextPreference>("username")?.run {
            setOnBindEditTextListener { editText ->
                editText.selectAll()
                editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(10))
            }
        }

        findPreference<AvatarPreference>("userAvatarURI")?.run {
            setOnPreferenceClickListener {
                showAvatarPickerDialog()
                true
            }
//            setOnPreferenceChangeListener { preference, newValue ->
//                setAvatar(Uri.parse(newValue as String))
//                true
//            }
        }

        findPreference<Preference>("clear_account")?.run {
            setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.clear_account_dialog_title)
                    .setMessage(R.string.clear_account_message)
                    .setPositiveButton(R.string.clear_account_positive) { dialogInterface: DialogInterface, i: Int ->
                        sharedPreference?.let {
                            it.edit()
                                .putInt("exp", 0)
                                .apply()
                        }
                        val job = Job()
                        CoroutineScope(job).launch {
                            val dataBase = AppDataBase.getDataBase()
                            dataBase.actionDao().deleteAll()
                            dataBase.achievementDao().deleteAll()
                        }
                        Toast.makeText(
                            context,
                            getString(R.string.clear_account_toast),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .setNegativeButton(R.string.clear_account_negative) { dialogInterface: DialogInterface, i: Int -> }
                    .show()
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
//            it.inflateMenu()
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

    private fun showAvatarPickerDialog() {
        val newFragment =
            AvatarPickerDialogFragment(object : AvatarPickerDialogFragment.AvatarChangedListener {
                override fun onAvatarChange() {
                    findPreference<AvatarPreference>("userAvatarURI")?.updateAvatar()
                }
            })
        newFragment.show(parentFragmentManager, "avatar")
    }
}