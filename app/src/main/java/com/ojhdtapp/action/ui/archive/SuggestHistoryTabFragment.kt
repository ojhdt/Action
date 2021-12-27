package com.ojhdtapp.action.ui.archive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialSharedAxis
import com.ojhdtapp.action.MyOnClickListener
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentSuggestHistoryTabBinding


class SuggestHistoryTabFragment : Fragment() {

    private var _binding: FragmentSuggestHistoryTabBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SuggestArchiveViewModel>()

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
            SuggestHistoryAdapter(emptyBtnListener = object :MyOnClickListener{
                override fun onClick() {
                    val bundle = bundleOf("IS_SHOWING_BOTTOMSHEETDIALOG" to true)
                    findNavController().navigate(R.id.action_suggestArchiveFragment_to_actionFragment,bundle)
                }
            })
//        val myLayoutManager =
//            object : LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
//                override fun canScrollVertically(): Boolean = false
//            }
        binding.recyclerView.run {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(context)
        }
        viewModel.suggestReadLive.observe(this) {
            val emptyList = listOf(null)
            if (it.isEmpty()) {
                myAdapter.submitList(emptyList)
            } else {
                myAdapter.submitList(it)
            }
        }
        viewModel.readSuggestRefresh()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}