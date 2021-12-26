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
import com.ojhdtapp.action.MyOnClickListener
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentSuggestArchiveTabBinding

class SuggestArchiveTabFragment : Fragment() {

    private var _binding: FragmentSuggestArchiveTabBinding? = null
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
        _binding = FragmentSuggestArchiveTabBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adapter for rv
        val myAdapter = SuggestArchiveAdapter(emptyBtnListener = object :
            MyOnClickListener {
            override fun onClick() {
                val bundle = bundleOf("IS_SHOWING_BOTTOMSHEETDIALOG" to true)
                findNavController().navigate(
                    R.id.action_suggestArchiveFragment_to_actionFragment,
                    bundle
                )
            }
        })
        binding.recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            adapter = myAdapter
        }

        viewModel.suggestArchivedLive.observe(this) {
            val emptyList = listOf(null)
            if (it.isEmpty()) {
                myAdapter.submitList(emptyList)
            } else {
                myAdapter.submitList(it)
            }
        }
        viewModel.archivedSuggestRefresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}