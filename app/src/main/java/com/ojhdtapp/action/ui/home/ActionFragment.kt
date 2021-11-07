package com.ojhdtapp.action.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentAchievementBinding
import com.ojhdtapp.action.databinding.FragmentActionBinding

class ActionFragment : Fragment() {
    var _binding: FragmentActionBinding? = null

    val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentActionBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}