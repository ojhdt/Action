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
import cn.leancloud.LCObject
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialElevationScale
import com.ojhdtapp.action.BaseApplication
import com.ojhdtapp.action.DeviceUtil
import com.ojhdtapp.action.R
import com.ojhdtapp.action.databinding.ActionBottomSheetDialogBinding
import com.ojhdtapp.action.databinding.FragmentActionBinding
import com.ojhdtapp.action.logic.AppDataBase
import com.ojhdtapp.action.logic.LeanCloudDataBase
import com.ojhdtapp.action.logic.Repository
import com.ojhdtapp.action.logic.model.Action
import com.ojhdtapp.action.logic.model.Suggest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Date
import java.sql.Timestamp
import java.util.concurrent.TimeUnit
import kotlin.math.round
import io.reactivex.disposables.Disposable




class ActionFragment : Fragment() {
    private var _binding: FragmentActionBinding? = null
    private var _bottomSheetDialogBinding: ActionBottomSheetDialogBinding? = null
    val viewModel: SharedViewModel by activityViewModels()
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val binding get() = _binding!!
    private val bottomSheetDialogBinding get() = _bottomSheetDialogBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentActionBinding.inflate(inflater, container, false)
        // Inflate the layout for the BottomSheetDialog
        _bottomSheetDialogBinding = ActionBottomSheetDialogBinding.inflate(inflater, null, false)
        bottomSheetDialog = BottomSheetDialog(
            requireContext(),
            R.style.ThemeOverlay_Material3_BottomSheetDialog
        ).apply {
            setContentView(bottomSheetDialogBinding.root)
        }
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

        // Set OnClickListener for BottomSheetDialog Btns
        bottomSheetDialogBinding.run {
            val lcJob = Job()
            var isSearching = false
            cardViewShuffle.setOnClickListener {
                if (!isSearching) {
                    CoroutineScope(lcJob).launch {
                        isSearching = true
                        viewModel.storeSuggestFromCloud(0)
                        isSearching = false
                    }
                }
            }
            cardViewNews.setOnClickListener {
                val newSuggest = LCObject("Suggest").apply {
                    put("title", "As Virus Cases Rise in Europe, an Economic Toll Returns")
                    put("subhead", "A series of restrictions, including a lockdown in Austria, is expected to put a brake on economic growth.")
                    put("imgUrl", "https://static01.nyt.com/images/2021/11/22/business/00virus-euroecon-1/merlin_198253512_8d84ab6c-6288-441c-bf4d-6e784ca170e6-superJumbo.jpg?quality=75&auto=webp")
                    put("time", Date(System.currentTimeMillis()))
                    put("authorAvatarUrl","https://static01.nyt.com/images/2018/02/16/multimedia/author-patricia-cohen/author-patricia-cohen-thumbLarge.jpg")
                    put("author", "Patricia Cohen")
                    put("source", "NY Times")
                    put("type", 0)
                    put("content","Europe’s already fragile economic recovery is at risk of being undermined by a fourth wave of coronavirus infections now dousing the continent, as governments impose increasingly stringent health restrictions that could reduce foot traffic in shopping centers, discourage travel and thin crowds in restaurants, bars and ski resorts.\n" +
                            "\n" +
                            "Austria has imposed the strictest measures, mandating vaccinations and imposing a nationwide lockdown that began on Monday. But economic activity will also be dampened by other safety measures — from vaccine passports in France and Switzerland to a requirement to work from home four days a week in Belgium.\n" +
                            "\n" +
                            "“We are expecting a bumpy winter season,” said Stefan Kooths, a research director of the Kiel Institute for the World Economy in Germany. “The pandemic now seems to be affecting the economy more negatively than we originally thought.”")
                    put("label", mapOf(R.drawable.ic_outline_emoji_events_24 to "WaterSave"))
                }
                newSuggest.saveInBackground().subscribe(object :io.reactivex.Observer<LCObject>{
                    override fun onSubscribe(d: Disposable) {
                        TODO("Not yet implemented")
                    }

                    override fun onNext(t: LCObject) {
                        TODO("Not yet implemented")
                    }

                    override fun onError(e: Throwable) {
                        TODO("Not yet implemented")
                    }

                    override fun onComplete() {
                        TODO("Not yet implemented")
                    }
                })
            }
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
        val suggestMoreAdapter =
            ActionAdapters.SuggestMoreAdapter(object : ActionAdapters.SuggestMoreListener {
                override fun onSuggestClick() {
                    exitTransition = Hold()
                    reenterTransition = Hold()
//                    exitTransition = MaterialElevationScale(false).apply {
//                        duration =
//                            resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
//                    }
//                    reenterTransition = MaterialElevationScale(true).apply {
//                        duration =
//                            resources.getInteger(R.integer.material_motion_duration_long_1).toLong()
//                    }
                }

            })
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
            bottomSheetDialog.show()

            val database = AppDataBase.getDataBase().suggestDao()
            val listA = listOf(
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
            val listB = listOf(
                Suggest(
                    title = "标题",
                    author = "Ojhdt",
                    authorAvatarUrl = "https://blog.ojhdt.com/images/avatar.png",
                    imgUrl = "https://tva2.sinaimg.cn/large/0072Vf1pgy1fodqgiodg1j31gs1191im.jpg",
                    label = listOf(
                        Pair(R.drawable.ic_outline_emoji_events_24, "WaterSave"),
                        Pair(R.drawable.ic_outline_emoji_events_24, "WaterSave")
                    ),
                ),
                Suggest(
                    imgUrl = "https://tva3.sinaimg.cn/large/0072Vf1pgy1foxkjiky1hj31hc0u0nhq.jpg",
                    label = listOf(
                        Pair(R.drawable.ic_outline_emoji_events_24, "WaterSave"),
                        Pair(R.drawable.ic_outline_emoji_events_24, "WaterSave")
                    ),
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _bottomSheetDialogBinding = null
    }
}
