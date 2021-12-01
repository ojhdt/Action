package com.ojhdtapp.action.ui.content

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.ojhdtapp.action.*
import com.ojhdtapp.action.databinding.FragmentSuggestContentBinding
import com.ojhdtapp.action.logic.AppDataBase
import com.ojhdtapp.action.logic.dao.SuggestDao
import com.ojhdtapp.action.logic.model.Suggest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.round

class SuggestContentFragment : Fragment() {

    lateinit var data: Suggest
    lateinit var database: SuggestDao
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
        //  Initialize Dao
        database = AppDataBase.getDataBase().suggestDao()
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
            binding.suggestContentTime.text =
                getString(R.string.suggest_content_date, DateUtil.formatDate(it.time))
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
                            R.style.Widget_Material3_Chip_Filter_Elevated
                        ), null, 0
                    ).apply {
                        text = it.value
                        iconStartPadding = DensityUtil.dip2px(context, 2f).toFloat()
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
            binding.confirnButton.text =
                if (!it.archived) getString(R.string.suggest_content_archive) else getString(R.string.suggest_content_archived)
        }

        // Btns Onclock
        binding.confirnButton.setOnClickListener {
            switchArchiveState()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Functions
    private fun updateData(newData:Suggest){
        Log.d("aaa", newData.archived.toString())
        arguments?.putParcelable("SUGGEST", newData)
        viewModel.sumbitData(newData)
        val job = Job()
        CoroutineScope(job).launch {
            database.updateSuggest(newData)
        }
        job.complete()
    }

    private fun switchArchiveState(){
        if (data.archived == false) {
            val newData = data.apply {
                archived = true
            }
            updateData(newData)
            Snackbar.make(
                binding.root,
                getString(R.string.suggest_content_archive_message),
                Snackbar.LENGTH_SHORT
            )
                .setAction(getString(R.string.revoke)
                ) {
                    val oldData = data.apply {
                        archived = false
                    }
                    updateData(oldData)
                }.show()
        } else {
            val newData = data.apply {
                archived = false
            }
            updateData(newData)
            Snackbar.make(
                binding.root,
                getString(R.string.suggest_content_archive_revoke_message),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}