package com.ojhdtapp.action.ui.content

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat.setTransitionName
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentActionContentBinding
import com.ojhdtapp.action.logic.AppDataBase
import com.ojhdtapp.action.logic.detector.AchievementPusher
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.util.DateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ActionContentFragment : Fragment() {
    lateinit var data: Action
    private var _binding: FragmentActionContentBinding? = null
    private val binding get() = _binding!!
    private val dataBase = AppDataBase.getDataBase().actionDao()
    val viewmodel by viewModels<ActionContentViewModel>()

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
        // Inflate the layout for this
        _binding = FragmentActionContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//         Share Element Transition
        setTransitionName(
            binding.actionContentContainer, getString(
                R.string.action_content_transition_name
            )
        )

        // Setup Appbar
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.actionFragment,
                R.id.achievementFragment,
                R.id.exploreFragment
            )
        )
        NavigationUI.setupWithNavController(
            binding.toolbar,
            findNavController(),
            appBarConfiguration
        )

        // Setup highlight RV
        val myAdapter = ActionContentHighlightAdapter()
        binding.highlights.run {
            layoutManager = LinearLayoutManager(context)
            adapter = myAdapter
        }

        // Load Data & Initialize ViewModel
        viewmodel.sumbitData(data)
        viewmodel.dataLive.observe(this) { it ->
//            startPostponedEnterTransition()
//            activity?.findViewById<MaterialToolbar>(R.id.actionContentToolbar)?.title = it.title
            binding.toolbar.title = it.title
            Glide.with(this)
                .load(it.imageUrl)
                .into(binding.imageFilterView)
            binding.chips.removeAllViews()
            data.label?.forEach {
                binding.chips.addView(Chip(binding.root.context).apply {
                    setChipIconResource(it.key)
                    text = it.value
                })
            }
            binding.highlights.removeAllViews()
            data.hightlight.run {
                if (size == 0 || size > 6) {
                    binding.highlight.visibility = View.GONE
                } else {
                    val numMap = mapOf(
                        0 to R.drawable.ic_outline_looks_one_24,
                        1 to R.drawable.ic_outline_looks_two_24,
                        2 to R.drawable.ic_outline_looks_3_24,
                        3 to R.drawable.ic_outline_looks_4_24,
                        4 to R.drawable.ic_outline_looks_5_24,
                        5 to R.drawable.ic_outline_looks_6_24
                    )
                    val list = mutableListOf<Pair<Int, String>>()
                    forEachIndexed { index, s ->
                        list.add(Pair(numMap[index]!!, s))
                    }
                    myAdapter.submitList(list)
                }
            }
            binding.content.text = it.content
            binding.label.text =
                binding.root.resources.getString(
                    R.string.pair_messages,
                    data.history.last().cause,
                    DateUtil.timeAgo(data.history.last().time)
                )

            // Btn State
            binding.confirnButton.run {
                isEnabled = it.isActivating
                text =
                    if (!it.history.last().finished) getString(R.string.action_content_fab_mark) else getString(
                        R.string.action_content_fab_unmark
                    )
            }
            binding.ignoreButton.run {
                isEnabled = it.isActivating
                text =
                    if (it.isActivating) getString(R.string.action_content_fab_ignore) else getString(
                        R.string.action_content_fab_ignored
                    )
            }
        }

        // Btn functions
        binding.confirnButton.setOnClickListener {
            switchState()
        }
        binding.ignoreButton.setOnClickListener {
            markIgnored()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Functions
    fun switchState() {
        if (data.isActivating) {
            if (data.history.last().finished) {
                data.history.last().finished = false
                markUnFinished()
            } else {
                data.history.last().finished = true
                markFinished()
            }
            viewmodel.sumbitData(data)
            Snackbar.make(
                binding.actionContentContainer,
                R.string.action_content_switch_finish_state,
                Snackbar.LENGTH_SHORT
            ).show()
            AchievementPusher.getPusher().tryPushingNewAchievement()
        }
    }

    private fun markFinished() {
        val newHistory = data.history.apply {
            last().finished = true
        }
        val job = Job()
        CoroutineScope(job).launch {
            dataBase.updateAction(data.apply {
                history = newHistory
            })
        }
        job.complete()
    }

    private fun markUnFinished() {
        val newHistory = data.history.apply {
            last().finished = false
        }
        val job = Job()
        CoroutineScope(job).launch {
            dataBase.updateAction(data.apply {
                history = newHistory
            })
        }
        job.complete()
    }

    private fun markIgnored() {
        val job = Job()
        CoroutineScope(job).launch {
            dataBase.updateAction(data.apply {
                isActivating = false
            })
        }
        job.complete()
    }
}