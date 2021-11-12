package com.ojhdtapp.action.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentAchievementBinding
import com.ojhdtapp.action.databinding.FragmentActionBinding
import com.ojhdtapp.action.logic.model.Action

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

        // Adapter for rv
        val headlineAdapter = ActionAdapters.HeadlineAdapter()
        val actionNowAdapter = ActionAdapters.ActionNowAdapter()
        val suggestMoreAdapter = ActionAdapters.SuggestMoreAdapter()
        val concatAdapter = ConcatAdapter(
            headlineAdapter,
            ActionAdapters.LabelAdapter(resources.getString(R.string.action_now_label),"根据你的状态推荐的行动"),
            actionNowAdapter,
            ActionAdapters.LabelAdapter(resources.getString(R.string.action_suggest_more), "更多有助于保护环境的建议"),
            suggestMoreAdapter
        )
        binding.actionRecyclerView.run {
            adapter = concatAdapter
            layoutManager = LinearLayoutManager(context)
        }

        headlineAdapter.submitList(listOf(ActionAdapters.HeadlineMessages("只要", "行动", "就永远不会太晚")))
        viewModel.actionNowLive.observe(this) {
            actionNowAdapter.submitList(it)
        }
        viewModel.suggestMoreLive.observe(this) {
            suggestMoreAdapter.submitList(it)
        }
        viewModel.refresh()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
