package com.ojhdtapp.action.ui.archive

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentActionArchiveHistoryBinding
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.util.DateUtil
import okhttp3.internal.filterList

class ActionArchiveHistoryFragment : Fragment() {
    lateinit var data: Action
    private var _binding: FragmentActionArchiveHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ActionArchiveHistoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getParcelable<Action>("ACTION") ?: Action()
        }
        // Add Transition
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host
            duration = resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(TypedValue().apply {
                context?.theme?.resolveAttribute(
                    android.R.attr.colorBackground,
                    this,
                    true
                )
            }.data)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentActionArchiveHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Share Element Transition
        ViewCompat.setTransitionName(
            binding.actionHistoryContentContainer, getString(
                R.string.action_history_transition_name
            )
        )
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
                R.id.refresh -> {}
                R.id.toContent -> {}
                else -> {}
            }
            false
        }

        // Setup Adapter for rv
        viewModel.submit(data)
        viewModel.actionArchiveLive.observe(this) {
            val contributionList = mutableListOf<Pair<Int, String>>()
            val historyTriggerList = mutableListOf<ActionArchiveHistoryAdapters.HistoryData>()
            it.history.forEach {
                val times = DateUtil.formatDateForDetail(it.time)
                historyTriggerList.add(
                    ActionArchiveHistoryAdapters.HistoryData(
                        getString(R.string.action_history_history_trigger_month, times[1]),
                        times[2],
                        it.cause
                    )
                )
            }
            binding.triggerdTime.text = it.history.size.toString()
            binding.finishedTime.text = it.history.filterList {
                this.finished
            }.size.toString()
            if (it.history.isNotEmpty()) {
                binding.lastTriggered.text = getString(
                    R.string.action_history_last_time,
                    DateUtil.timeAgo(it.history.last().time)
                )
            }
            binding.contributionRecyclerView.run {
                val myAdapter = ActionArchiveHistoryAdapters.ContributionAdapter()
                val list = mutableListOf<Pair<Int, String>>()
                layoutManager = LinearLayoutManager(context)
                adapter = myAdapter
                myAdapter.submitList(list)
            }
            binding.triggerRecyclerView.run {
                val myAdapter = ActionArchiveHistoryAdapters.HistoryAdapter()
                layoutManager = LinearLayoutManager(context)
                adapter = myAdapter
                myAdapter.submitList(historyTriggerList)
            }
        }

        // FAB
        binding.toContentFAB.transitionName =
            getString(R.string.action_history_transition_name, data.id.toString())
        binding.toContentFAB.setOnClickListener {
            val bundle = bundleOf("ACTION" to data)
            val actionContentTransitionName =
                binding.root.resources.getString(R.string.action_content_transition_name)
            val extras =
                FragmentNavigatorExtras(binding.toContentFAB to actionContentTransitionName)
            navController.navigate(
                R.id.action_actionArchiveHistoryFragment_to_actionContentFragment,
                bundle, null, extras
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}