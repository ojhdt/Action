package com.ojhdtapp.action.ui.dialog

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.AboutDialogBinding
import com.ojhdtapp.action.databinding.AvatarPickerDialogBinding
import com.ojhdtapp.action.getUriToDrawable

class AvatarPickerDialogFragment(val listener: AvatarChangedListener) : DialogFragment() {

    interface AvatarChangedListener {
        fun onAvatarChange()
    }

    private var _binding: AvatarPickerDialogBinding? = null
    private val binding get() = _binding!!
    private val sharedPreference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            dismiss()
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val uri = data?.data
                Log.d("aaa", uri.toString())
                uri?.let {
                    updateUri(it)
                }
            }
        }

    private fun openAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private fun selectAvatar() {
        if (ContextCompat.checkSelfPermission(
                context!!,
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Permission
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
                    openAlbum()
                } else {
                    dismiss()
                    Toast.makeText(
                        context,
                        getString(R.string.welcome_useravatar_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            _binding = AvatarPickerDialogBinding.inflate(inflater, null, false)

            binding.run {
                // Selections
                Glide.with(context!!)
                    .load(R.drawable.avatar_a)
                    .into(avatarDialogSelectionA)
                Glide.with(context!!)
                    .load(R.drawable.avatar_b)
                    .into(avatarDialogSelectionB)
                Glide.with(context!!)
                    .load(R.drawable.avatar_c)
                    .into(avatarDialogSelectionC)
                Glide.with(context!!)
                    .load(R.drawable.avatar_d)
                    .into(avatarDialogSelectionD)
                Glide.with(context!!)
                    .load(R.drawable.avatar_e)
                    .into(avatarDialogSelectionE)
                Glide.with(context!!)
                    .load(R.drawable.avatar_f)
                    .into(avatarDialogSelectionF)
                Glide.with(context!!)
                    .load(R.drawable.avatar_g)
                    .into(avatarDialogSelectionG)

                avatarDialogSelectionA.setOnClickListener {
                    updateUri(getUriToDrawable(R.drawable.avatar_a))
                    dismiss()
                }
                avatarDialogSelectionB.setOnClickListener {
                    updateUri(getUriToDrawable(R.drawable.avatar_b))
                    dismiss()
                }
                avatarDialogSelectionC.setOnClickListener {
                    updateUri(getUriToDrawable(R.drawable.avatar_c))
                    dismiss()
                }
                avatarDialogSelectionD.setOnClickListener {
                    updateUri(getUriToDrawable(R.drawable.avatar_d))
                    dismiss()
                }
                avatarDialogSelectionE.setOnClickListener {
                    updateUri(getUriToDrawable(R.drawable.avatar_e))
                    dismiss()
                }
                avatarDialogSelectionF.setOnClickListener {
                    updateUri(getUriToDrawable(R.drawable.avatar_f))
                    dismiss()
                }
                avatarDialogSelectionG.setOnClickListener {
                    updateUri(getUriToDrawable(R.drawable.avatar_g))
                    dismiss()
                }
                avatarDialogSelectionH.setOnClickListener {
                    selectAvatar()
                }
            }

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(binding.root)
                .setTitle(getString(R.string.avatar_dialog_title))
                .setNegativeButton(getString(R.string.avatar_dialog_negative)) { dialogInterface: DialogInterface, i: Int -> }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updateUri(uri: Uri) {
        sharedPreference.edit()
            .putString("userAvatarURI", uri.toString())
            .apply()
        listener.onAvatarChange()
    }
}