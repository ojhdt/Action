package com.ojhdtapp.action.ui.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.DeviceUtil
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentActionContentBinding
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.ui.home.ActionContentViewModel

class ActionContentFragment : Fragment() {
    lateinit var data: Action
    private var _binding: FragmentActionContentBinding? = null
    private val binding get() = _binding!!
    val viewmodel by viewModels<ActionContentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getParcelable<Action>("ACTION") ?: Action()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this
        _binding = FragmentActionContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // FAB Switch
//        binding.floatingActionButton.setOnClickListener {
//            viewmodel.switchFinishedStatus()
//        }
        // Setup Appbar
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.actionFragment,
                R.id.achievementFragment,
                R.id.exploreFragment
            )
        )
        NavigationUI.setupWithNavController(
            binding.actionContentToolbar,
            findNavController(),
            appBarConfiguration
        )
        binding.appbar.apply {
            val offset = DeviceUtil.getStatusBarHeight(BaseApplication.context)
            setPadding(0, offset, 0, 0)
        }
        // Load Data & Initialize ViewModel
        viewmodel.sumbitData(data)
        viewmodel.dataLive.observe(this) { it ->
            activity?.findViewById<MaterialToolbar>(R.id.actionContentToolbar)?.title = it.title
            Glide.with(this)
                .load(it.imageID)
                .into(binding.imageView2)
            binding.chips.removeAllViews()
            data.label.forEach {
                binding.chips.addView(Chip(binding.root.context).apply {
                    text = it.second
                    it.first?.let {
                        setChipIconResource(it)
                    }
                })
            }
            binding.content.text = it.content
            binding.label.text = getString(R.string.pair_messages, it.source, it.timestamp)
//            if (it.finished) {
//                binding.floatingActionButton.icon =
//                    ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_outline_undo_24)
//                binding.floatingActionButton.text = getString(R.string.action_content_fab_unmark)
//            } else {
//                binding.floatingActionButton.icon =
//                    ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_outline_done_24)
//                binding.floatingActionButton.text = getString(R.string.action_content_fab_mark)
//            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}