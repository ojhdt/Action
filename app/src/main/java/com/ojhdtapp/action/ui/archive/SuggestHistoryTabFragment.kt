package com.ojhdtapp.action.ui.archive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentSuggestHistoryTabBinding
import com.ojhdtapp.action.ui.home.ActionAdapters


class SuggestHistoryTabFragment : Fragment() {

    private var _binding: FragmentSuggestHistoryTabBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SuggestHistoryTabViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSuggestHistoryTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adapter for rv
        val myAdapter =
            ActionAdapters.SuggestMoreAdapter(object : ActionAdapters.SuggestMoreListener {
                override fun onSuggestClick() {
                    TODO("Not yet implemented")
                }
            })
        binding.recyclerView.run {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(context)
        }
        viewModel.suggestReadLive.observe(this) {
            myAdapter.submitList(it)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}