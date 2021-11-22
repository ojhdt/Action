package com.ojhdtapp.action.ui.home

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialElevationScale
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.DeviceUtil
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentActionBinding
import com.ojhdtapp.action.logic.AppDataBase
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.Action
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Date
import java.sql.Timestamp
import java.util.concurrent.TimeUnit

class ActionFragment : Fragment() {
    private var _binding: FragmentActionBinding? = null
    val viewModel: SharedViewModel by activityViewModels()

    private val binding get() = _binding!!

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
        // Postpone Transition
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        // Set StatusBar Offset
        binding.welcomeTextContainer.run {
            val offset = DeviceUtil.getStatusBarHeight(BaseApplication.context)
            setPadding(paddingLeft, paddingTop + offset, paddingRight, paddingBottom)
        }

        // Adapter for rv
        val headlineAdapter = ActionAdapters.HeadlineAdapter()
        val actionNowAdapter =
            ActionAdapters.ActionNowAdapter(object : ActionAdapters.ActionNowListener {
                override fun onActionCLick() {
                    exitTransition = MaterialElevationScale(false).apply {
                        duration =
                            resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
                    }
                    reenterTransition = MaterialElevationScale(true).apply {
                        duration =
                            resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
                    }
                }
            })
        val suggestMoreAdapter = ActionAdapters.SuggestMoreAdapter()
        val concatAdapter = ConcatAdapter(
            headlineAdapter,
            ActionAdapters.LabelAdapter(
                resources.getString(R.string.action_now_label),
                resources.getString(R.string.action_now_label_description)
            ),
            actionNowAdapter,
            ActionAdapters.LabelAdapter(
                resources.getString(R.string.action_suggest_more),
                resources.getString(R.string.action_suggest_more_description)
            ),
            suggestMoreAdapter
        )
        binding.actionRecyclerView.run {
            adapter = concatAdapter
            layoutManager = LinearLayoutManager(context)
        }

        resources.run {
            val headlineMessages = listOf<String>(
                getString(R.string.action_title_a),
                getString(R.string.action_title_b),
                getString(R.string.action_title_c)
            )
            headlineAdapter.submitList(
                listOf(
                    ActionAdapters.HeadlineMessages(
                        headlineMessages[0],
                        headlineMessages[1],
                        headlineMessages[2]
                    )
                )
            )
        }
        viewModel.actionNowLive.observe(this) {
            actionNowAdapter.submitList(it)
        }
        viewModel.suggestMoreLive.observe(this) {
            suggestMoreAdapter.submitList(it)
//            (view.parent as? ViewGroup)?.doOnPreDraw {
//                startPostponedEnterTransition()
//            }
        }

        // Welcome Text & Avatar
        viewModel.userInfoLive.observe(this) {
            binding.welcomeTextView.text =
                resources.getStringArray(R.array.action_welcomes).random() + it.username
            Glide.with(BaseApplication.context)
                .load(ContextCompat.getDrawable(BaseApplication.context, it.avatarResID))
                .into(binding.welcomeAvatarImageView)
        }

        // FAB Show & Hide
        binding.actionRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    // Scroll Down
                    if (binding.floatingActionButton.isExtended) {
                        binding.floatingActionButton.shrink();
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!binding.floatingActionButton.isExtended) {
                        binding.floatingActionButton.extend();
                    }
                }
            }
        })

        binding.floatingActionButton.setOnClickListener {
            val database = AppDataBase.getDataBase().actionDao()
            val list = listOf(
                Action(
                    "McCarthy blasts Democrats, stalls Biden bill in over-8-hour tirade on House floor",
                    R.drawable.city,
                    getString(R.string.lorem_ipsum),
                    "位置信息", Date(System.currentTimeMillis()),
                    listOf(
                        Pair(R.drawable.ic_outline_emoji_events_24, "WaterSave"),
                        Pair(R.drawable.ic_outline_emoji_events_24, "WaterSave")
                    ),
                    listOf("第一条", "第二条", "第三条")
                ),
                Action(
                    "勤关水龙头",
                    R.drawable.anonymous,
                    "一些内容",
                    "位置信息",
                    Date(System.currentTimeMillis()),
                )
            )
            val job = Job()
            val scope = CoroutineScope(job)
            scope.launch {
                list.forEach {
                    database.insertAction(it)
                }
            }
            job.complete()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
