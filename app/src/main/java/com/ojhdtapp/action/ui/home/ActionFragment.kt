package com.ojhdtapp.action.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentAchievementBinding
import com.ojhdtapp.action.databinding.FragmentActionBinding

class ActionFragment : Fragment() {
    private var _binding: FragmentActionBinding? = null
    val viewModel: ActionViewModel by viewModels()

    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentActionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val headlineAdapter = HeadlineAdapter()
        binding.actionRecyclerView.run {
            adapter = headlineAdapter
            layoutManager = LinearLayoutManager(context)
        }
        headlineAdapter.submitList(listOf(HeadlineMessages("只要","行动","就永远不会太晚")))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}