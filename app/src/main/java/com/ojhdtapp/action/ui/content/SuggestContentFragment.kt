package com.ojhdtapp.action.ui.content

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.transition.MaterialContainerTransform
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.DeviceUtil
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.FragmentSuggestContentBinding
import com.ojhdtapp.action.logic.model.Suggest

class SuggestContentFragment : Fragment() {

    lateinit var data: Suggest
    private var _binding: FragmentSuggestContentBinding? = null
    private val binding get() = _binding!!
    val viewModel by viewModels<SuggestContentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getParcelable<Suggest>("SUGGEST") ?: Suggest()
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
        _binding = FragmentSuggestContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Share Element Transition
        ViewCompat.setTransitionName(
            binding.suggestContentContainer, getString(
                R.string.suggest_content_transition_name
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

        // Load Data & Initialize ViewModel
        viewModel.sumbitData(data)
        viewModel.dataLive.observe(this) { it ->
            it.imgUrl?.let {
                Glide.with(this)
                    .load(it)
//                .placeholder()
                    .into(binding.toolbarImageView)
            }
            binding.suggestContentTitle.text = it.title
            binding.suggestContentTime.text = it.time.time.toString()
//                getString(R.string.pair_messages, it.author, it.time.time.toString())
            it.authorAvatarUrl?.let {
                Glide.with(this)
                    .load(it)
                    .into(binding.authorImage)
            }
            binding.authorName.text = it.author
            binding.authorSource.text = it.source
            binding.chips.removeAllViews()
            data.label?.forEach {
                binding.chips.addView(
                    Chip(
                        ContextThemeWrapper(
                            binding.root.context,
                            R.style.Widget_Material3_Chip_Filter
                        ), null, 0
                    ).apply {
                        text = it.value
//                        setChipBackgroundColorResource(TypedValue().apply {
//                            context.theme.resolveAttribute(
//                                android.R.attr.colorPrimary,
//                                this,
//                                true
//                            )
//                        }.data)
                        it.key.let {
                            setChipIconResource(it)
                        }
                    })
            }
            binding.content.text = it.content
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}