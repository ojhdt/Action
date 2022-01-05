package com.ojhdtapp.action.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.AboutDialogBinding
import android.content.pm.PackageManager

import android.content.pm.PackageInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class VersionDialogFragment : DialogFragment() {
    private var _binding: AboutDialogBinding? = null
    val binding get() = _binding!!
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            _binding = AboutDialogBinding.inflate(inflater, null, false)
            val packageManager = context!!.packageManager
            binding.appVersionCode.text = try {
                val packageInfo = packageManager.getPackageInfo(context!!.packageName, 0)
                packageInfo.longVersionCode.toString()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                "ERROR"
            }
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}