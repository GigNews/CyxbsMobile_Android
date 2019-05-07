package com.mredrock.cyxbs.qa.pages.comment.ui

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.isSuccessful
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.comment.AdoptAnswerEvent
import com.mredrock.cyxbs.qa.pages.comment.viewmodel.CommentListViewModel
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.utils.setPraise
import kotlinx.android.synthetic.main.qa_activity_comment_list.*
import kotlinx.android.synthetic.main.qa_dialog_comment_bottom_sheet_dialog.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class CommentListActivity : BaseViewModelActivity<CommentListViewModel>() {
    companion object {
        const val REQUEST_CODE = 0x123

        fun activityStart(activity: Activity,
                          question: Question,
                          answer: Answer) {
            val title = "<font color=\"#7195fa\">#${question.tags}#</font>${question.title}"
            val showAdoptIcon = question.hasAdoptedAnswer || !question.isSelf
            activity.startActivityForResult<CommentListActivity>(REQUEST_CODE,
                    "qid" to question.id,
                    "title" to title,
                    "showAdoptIcon" to showAdoptIcon,
                    "isEmotion" to question.isEmotion,
                    "answer" to answer
            )
        }
    }

    override val viewModelClass = CommentListViewModel::class.java
    override val isFragmentActivity = false

    private lateinit var headerAdapter: CommentListHeaderRvAdapter
    private lateinit var emptyRvAdapter: EmptyRvAdapter
    private lateinit var footerRvAdapter: FooterRvAdapter
    private lateinit var commentListRvAdapter: CommentListRvAdapter

    private val commentBottomSheetDialog by lazy { createCommentDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_comment_list)

        val title = intent.getStringExtra("title")
        val showAdoptIcon = intent.getBooleanExtra("showAdoptIcon", false)
        val isEmotion = intent.getBooleanExtra("isEmotion", false)
        common_toolbar.init(getString(R.string.qa_comment_list_title))
        initRv(title, showAdoptIcon, isEmotion)
        initBottomView()
    }

    private fun initBottomView() {
        fl_praise.setOnClickListener { viewModel.clickPraiseButton() }
        fl_comment.setOnClickListener {
            card_footer.gone()
            commentBottomSheetDialog.show()
        }

        viewModel.apply {
            isPraised.observeNotNull { setPraise(null, it) }

            praiseCount.observeNotNull {
                setPraise(getString(R.string.qa_comment_list_praise_count, it), null)
            }

            commentCount.observeNotNull {
                commentBottomSheetDialog.dismiss()
                headerAdapter.notifyItemChanged(0)
                tv_comment.text = getString(R.string.qa_comment_list_comment_count, it)
            }

            refreshPreActivityEvent.observeNotNull {
                setResult(Activity.RESULT_OK)
                headerAdapter.notifyItemChanged(0)
            }
        }
    }

    private fun setPraise(text: String?, isPraised: Boolean?) = tv_praise.setPraise(text, isPraised,
            R.drawable.qa_ic_comment_list_praise, R.drawable.qa_ic_comment_list_praised)

    private fun initRv(title: String, showAdoptIcon: Boolean, isEmotion: Boolean) {
        headerAdapter = CommentListHeaderRvAdapter(title, isEmotion, showAdoptIcon)
        emptyRvAdapter = EmptyRvAdapter("还没有评论哦~")
        commentListRvAdapter = CommentListRvAdapter(isEmotion)
        footerRvAdapter = FooterRvAdapter { viewModel.retryFailedListRequest() }
        val adapterWrapper = RvAdapterWrapper(commentListRvAdapter, headerAdapter, footerRvAdapter, emptyRvAdapter)
        rv_comment_list.apply {
            layoutManager = LinearLayoutManager(this@CommentListActivity)
            adapter = adapterWrapper
        }
        swipe_refresh_layout.setOnRefreshListener { viewModel.invalidateCommentList() }
        observeListChangeEvent()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun adoptAnswer(event: AdoptAnswerEvent) {
        viewModel.adoptAnswer(event.aId)
    }


    private fun observeListChangeEvent() = viewModel.apply {
        answerLiveData.observeNotNull { headerAdapter.refreshData(listOf(it)) }

        commentList.observe { commentListRvAdapter.submitList(it) }

        networkState.observeNotNull { footerRvAdapter.refreshData(listOf(it)) }

        initialLoad.observe {
            when (it) {
                NetworkState.LOADING -> {
                    swipe_refresh_layout.isRefreshing = true
                    emptyRvAdapter.showHolder(3)
                }
                else -> {
                    swipe_refresh_layout.isRefreshing = false
                    emptyRvAdapter.hideHolder()
                }
            }
        }
    }

    private fun createCommentDialog() = BottomSheetDialog(this).apply {
        setContentView(R.layout.qa_dialog_comment_bottom_sheet_dialog)
        setOnDismissListener { this@CommentListActivity.card_footer.visible() }
        setOnCancelListener { viewModel.saveToDraft(edt_comment_content.text.toString()) }
        tv_send_comment.setOnClickListener { viewModel.sendComment(edt_comment_content.text.toString()) }
    }

    override fun getViewModelFactory(): ViewModelProvider.Factory? {
        val qid = intent.getStringExtra("qid")
        val answer = intent.getParcelableExtra<Answer>("answer")

        return CommentListViewModel.Factory(qid, answer)
    }
}
