package com.ojhdtapp.action.ui.content

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import cn.leancloud.LCObject
import cn.leancloud.LCQuery
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.ojhdtapp.action.*
import com.ojhdtapp.action.databinding.FragmentSuggestContentBinding
import com.ojhdtapp.action.logic.AppDataBase
import com.ojhdtapp.action.logic.LeanCloudDataBase
import com.ojhdtapp.action.logic.dao.SuggestDao
import com.ojhdtapp.action.logic.model.Suggest
import com.ojhdtapp.action.util.DateUtil
import com.ojhdtapp.action.util.DensityUtil
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import kotlin.math.round

class SuggestContentFragment : Fragment() {

    lateinit var data: Suggest
    lateinit var database: SuggestDao
    private var _binding: FragmentSuggestContentBinding? = null
    private val binding get() = _binding!!
    val viewModel by viewModels<SuggestContentViewModel>()
    private val sharedPreference: SharedPreferences? by lazy {
        context?.let {
            PreferenceManager.getDefaultSharedPreferences(it)
        }
    }

    private var processing = false

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
        // Initialize Dao
        database = AppDataBase.getDataBase().suggestDao()

        // Fetch VotingNum
        updateVotingNum()
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

        // Appbar Onclick
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.refresh -> syncSuggest()
                R.id.vote -> voteLike()
                R.id.ignore -> deleteSuggest()
                R.id.read -> switchReadState()
                R.id.archive -> switchArchiveState()
                R.id.share -> {}
                R.id.source -> redictToSourceUrl()
                else -> {}
            }
            false
        }

        // Auto Switch State
        if (sharedPreference?.getBoolean("suggest_auto_mark", false) == true) {
            switchReadState()
        }

        // Load Data & Initialize ViewModel
        viewModel.sumbitData(data)
        viewModel.dataLive.observe(viewLifecycleOwner) { it ->
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
            resetVotingStatus()
            binding.suggestContentThumbUpNum.text = it.like.toString()
            binding.suggestContentThumbDownNum.text = it.dislike.toString()
            binding.content.text = it.content
            binding.confirnButton.text =
                if (!it.archived) getString(R.string.suggest_content_archive) else getString(R.string.suggest_content_archived)
            binding.ignoreButton.run {
                text =
                    if (!it.read) getString(R.string.suggest_content_read) else getString(R.string.suggest_content_read_marked)
                isEnabled = !it.read
//                setTextColor(
//                    ContextCompat.getColor(
//                        context,
//                        R.color.m3_default_color_secondary_text
//                    )
//                )
            }
        }

        // Btns OnClick
        binding.confirnButton.setOnClickListener {
            switchArchiveState()
        }
        binding.ignoreButton.setOnClickListener {
            switchReadState()
        }
        binding.suggestContentThumbUp.setOnClickListener {
            voteLike()
        }
        binding.suggestContentThumbDown.setOnClickListener {
            voteDislike()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Functions
    private fun updateData(newData: Suggest) {
        arguments?.putParcelable("SUGGEST", newData)
        viewModel.sumbitData(newData)
        val job = Job()
        CoroutineScope(job).launch {
            database.updateSuggest(newData)
        }
        job.complete()
    }

    private fun updateVotingNum() {
        val query = LCQuery<LCObject>("Suggest")
        query.getInBackground(data.objectId).subscribe(object : Observer<LCObject> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: LCObject) {
                val newData = data.apply {
                    like = t.getInt("like")
                    dislike = t.getInt("dislike")
                }
                viewModel.sumbitData(newData)
            }

            override fun onError(e: Throwable) {
                Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onComplete() {
            }
        })
    }

    private fun switchArchiveState() {
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
                .setAction(
                    getString(R.string.revoke)
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

    private fun switchReadState() {
        if (!data.read) {
            val newData = data.apply {
                read = true
            }
            updateData(newData)
            Snackbar.make(
                binding.suggestContentContainer,
                R.string.suggest_content_switch_read_state,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun voteLike() {
        if (!processing) {
            processing = true
            val oldLikeNum = data.like
            val newLikeNumAdd = data.like + 1
            val newLikeNumSubtract = data.like - 1
            val oldDislikeNum = data.dislike
            val newDislikeNum = data.dislike - 1
            when (data.votingStatus) {
                1 -> {
                    val newData = data.apply {
                        like = newLikeNumSubtract
                        votingStatus = 0
                    }
                    arguments?.putParcelable("SUGGEST", newData)
                    viewModel.sumbitData(newData)
                    val lcObject = LCObject.createWithoutData("Suggest", data.objectId).apply {
                        increment("like", -1)
                    }
                    lcObject.saveInBackground().subscribe(object : Observer<LCObject> {
                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(t: LCObject) {
                            val job = Job()
                            CoroutineScope(job).launch {
                                database.updateSuggest(newData)
                            }
                            job.complete()
                        }

                        override fun onError(e: Throwable) {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.network_error),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            val oldData = data.apply {
                                like = oldLikeNum
                                votingStatus = 1
                            }
                            arguments?.putParcelable("SUGGEST", oldData)
                            viewModel.sumbitData(oldData)
                            processing = false
                        }

                        override fun onComplete() {
                            processing = false
                        }
                    })
                }
                2 -> {
                    val newData = data.apply {
                        like = newLikeNumAdd
                        dislike = newDislikeNum
                        votingStatus = 1
                    }
                    arguments?.putParcelable("SUGGEST", newData)
                    viewModel.sumbitData(newData)
                    val lcObject = LCObject.createWithoutData("Suggest", data.objectId).apply {
                        increment("like")
                        increment("dislike", -1)
                    }
                    lcObject.saveInBackground().subscribe(object : Observer<LCObject> {
                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(t: LCObject) {
                            val job = Job()
                            CoroutineScope(job).launch {
                                database.updateSuggest(newData)
                            }
                            job.complete()
                        }

                        override fun onError(e: Throwable) {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.network_error),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            val oldData = data.apply {
                                like = oldLikeNum
                                dislike = oldDislikeNum
                                votingStatus = 2
                            }
                            arguments?.putParcelable("SUGGEST", oldData)
                            viewModel.sumbitData(oldData)
                            processing = false
                        }

                        override fun onComplete() {
                            processing = false
                        }
                    })
                }
                else -> {
                    val newData = data.apply {
                        like = newLikeNumAdd
                        votingStatus = 1
                    }
                    arguments?.putParcelable("SUGGEST", newData)
                    viewModel.sumbitData(newData)
                    val lcObject = LCObject.createWithoutData("Suggest", data.objectId).apply {
                        increment("like")
                    }
                    lcObject.saveInBackground().subscribe(object : Observer<LCObject> {
                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(t: LCObject) {
                            val job = Job()
                            CoroutineScope(job).launch {
                                database.updateSuggest(newData)
                            }
                            job.complete()
                        }

                        override fun onError(e: Throwable) {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.network_error),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            val oldData = data.apply {
                                like = oldLikeNum
                                votingStatus = 0
                            }
                            arguments?.putParcelable("SUGGEST", oldData)
                            viewModel.sumbitData(oldData)
                            processing = false
                        }

                        override fun onComplete() {
                            processing = false
                        }
                    })
                }
            }
        } else {
            Toast.makeText(context, getString(R.string.processing), Toast.LENGTH_SHORT).show()
            resetVotingStatus()
        }
    }

    private fun voteDislike() {
        if (!processing) {
            processing = true
            val oldDislikeNum = data.dislike
            val newDislikeNumAdd = data.dislike + 1
            val newDislikeNumSubtract = data.dislike - 1
            val oldLikeNum = data.like
            val newLikeNum = data.like - 1
            when (data.votingStatus) {
                1 -> {
                    val newData = data.apply {
                        like = newLikeNum
                        dislike = newDislikeNumAdd
                        votingStatus = 2
                    }
                    arguments?.putParcelable("SUGGEST", newData)
                    viewModel.sumbitData(newData)
                    val lcObject = LCObject.createWithoutData("Suggest", data.objectId).apply {
                        increment("like", -1)
                        increment("dislike")
                    }
                    lcObject.saveInBackground().subscribe(object : Observer<LCObject> {
                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(t: LCObject) {
                            val job = Job()
                            CoroutineScope(job).launch {
                                database.updateSuggest(newData)
                            }
                            job.complete()
                        }

                        override fun onError(e: Throwable) {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.network_error),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            val oldData = data.apply {
                                like = oldLikeNum
                                dislike = oldDislikeNum
                                votingStatus = 1
                            }
                            arguments?.putParcelable("SUGGEST", oldData)
                            viewModel.sumbitData(oldData)
                            processing = false
                        }

                        override fun onComplete() {
                            processing = false
                        }
                    })
                }
                2 -> {
                    val newData = data.apply {
                        dislike = newDislikeNumSubtract
                        votingStatus = 0
                    }
                    arguments?.putParcelable("SUGGEST", newData)
                    viewModel.sumbitData(newData)
                    val lcObject = LCObject.createWithoutData("Suggest", data.objectId).apply {
                        increment("dislike", -1)
                    }
                    lcObject.saveInBackground().subscribe(object : Observer<LCObject> {
                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(t: LCObject) {
                            val job = Job()
                            CoroutineScope(job).launch {
                                database.updateSuggest(newData)
                            }
                            job.complete()
                        }

                        override fun onError(e: Throwable) {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.network_error),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            val oldData = data.apply {
                                dislike = oldDislikeNum
                                votingStatus = 2
                            }
                            arguments?.putParcelable("SUGGEST", oldData)
                            viewModel.sumbitData(oldData)
                            processing = false
                        }

                        override fun onComplete() {
                            processing = false
                        }
                    })
                }
                else -> {
                    val newData = data.apply {
                        dislike = newDislikeNumAdd
                        votingStatus = 2
                    }
                    arguments?.putParcelable("SUGGEST", newData)
                    viewModel.sumbitData(newData)
                    val lcObject = LCObject.createWithoutData("Suggest", data.objectId).apply {
                        increment("dislike")
                    }
                    lcObject.saveInBackground().subscribe(object : Observer<LCObject> {
                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(t: LCObject) {
                            val job = Job()
                            CoroutineScope(job).launch {
                                database.updateSuggest(newData)
                            }
                            job.complete()
                        }

                        override fun onError(e: Throwable) {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.network_error),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            val oldData = data.apply {
                                dislike = oldDislikeNum
                                votingStatus = 0
                            }
                            arguments?.putParcelable("SUGGEST", oldData)
                            viewModel.sumbitData(oldData)
                            processing = false
                        }

                        override fun onComplete() {
                            processing = false
                        }
                    })
                }
            }
        } else {
            Toast.makeText(context, getString(R.string.processing), Toast.LENGTH_SHORT).show()
            resetVotingStatus()
        }
    }

    private fun syncSuggest() {
        data.objectId?.let {
            val job = Job()
            CoroutineScope(job).launch {
                try {
                    val result = withContext(Dispatchers.IO) {
                        LeanCloudDataBase.syncSuggest(it)
                    }
                    arguments?.putParcelable("SUGGEST", result)
                    data.apply {
                        like = result.like
                        dislike = result.dislike
                    }
                    viewModel.sumbitDataThroughPost(result)
                    Snackbar.make(
                        binding.root,
                        getString(R.string.network_success),
                        Snackbar.LENGTH_SHORT
                    ).show()

                } catch (e: Exception) {
//                    Log.d("aaa",e.toString())
                    Snackbar.make(
                        binding.root,
                        getString(R.string.network_error),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun deleteSuggest() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(R.string.suggest_dialog_delete_title)
                .setMessage(R.string.suggest_dialog_delete_content)
                .setNegativeButton(R.string.cancel) { dialogInterface: DialogInterface, i: Int -> }
                .setPositiveButton(R.string.confirm) { dialogInterface: DialogInterface, i: Int ->
                    findNavController().navigateUp()
                    updateData(data.apply {
                        deleted = true
                    })
                }
                .show()
        }
    }

    private fun resetVotingStatus() {
        when (data.votingStatus) {
            1 -> {
                binding.suggestContentThumbUp.isChecked = true
                binding.suggestContentThumbDown.isChecked = false
            }
            2 -> {
                binding.suggestContentThumbUp.isChecked = false
                binding.suggestContentThumbDown.isChecked = true
            }
            else -> {
                binding.suggestContentThumbUp.isChecked = false
                binding.suggestContentThumbDown.isChecked = false
            }
        }
    }

    private fun redictToSourceUrl() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(this@SuggestContentFragment.data.sourceUrl)
        }
        startActivity(intent)
    }
}