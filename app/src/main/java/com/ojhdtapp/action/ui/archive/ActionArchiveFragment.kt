package com.ojhdtapp.action.ui.archive

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialSharedAxis
import com.ojhdtapp.action.MyOnClickListener
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentActionArchiveBinding

class ActionArchiveFragment : Fragment() {

    private var _binding: FragmentActionArchiveBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ActionArchiveViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        // Setup Transition
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentActionArchiveBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Postpone Transition
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        // Setup Appbar
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupWithNavController(
            binding.toolbar,
            navController,
            appBarConfiguration
        )
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.refresh -> {
                    try {
                        viewModel.actionRefresh()
                        Snackbar.make(
                            binding.root,
                            getString(R.string.network_success),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.network_error),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
                else -> {}
            }
            false
        }
        // Adapter for rv
        val myAdapter = ActionArchiveAdapter(object : MyOnClickListener {
            override fun onClick() {
                exitTransition = Hold()
                reenterTransition = Hold()
            }
        }, object : MyOnClickListener {
            override fun onClick() {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                    duration =
                        resources.getInteger(R.integer.material_motion_duration_long_1)
                            .toLong()
                }
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                    duration =
                        resources.getInteger(R.integer.material_motion_duration_long_1)
                            .toLong()
                }
            }
        })
        val myLayoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.run {
            adapter = myAdapter
            layoutManager = myLayoutManager
            addItemDecoration(myAdapter.ActionArchiveSpaceItemDecoration())
        }
        viewModel.actionLive.observe(this) {
            val emptyList = listOf(null)
            if (it.isEmpty()) {
                myLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int = 2
                }
                myAdapter.submitList(emptyList)
            } else {
                myLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int = 1
                }
                myAdapter.submitList(it)
            }
        }
        viewModel.actionRefresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}