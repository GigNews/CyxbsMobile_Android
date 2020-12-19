package com.mredrock.cyxbs.qa.pages.dynamic.ui.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.beannew.Dynamic
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.dynamic.ui.adapter.CommentListAdapter
import com.mredrock.cyxbs.qa.pages.dynamic.viewmodel.DynamicDetailViewModel
import com.mredrock.cyxbs.qa.ui.activity.ViewImageActivity
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import com.mredrock.cyxbs.qa.ui.widget.*
import com.mredrock.cyxbs.qa.utils.KeyboardController
import com.mredrock.cyxbs.qa.utils.dynamicTimeDescription
import kotlinx.android.synthetic.main.qa_activity_dynamic_detail.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_header.*
import kotlinx.android.synthetic.main.qa_recycler_item_dynamic_header.view.*


/**
 * @Author: zhangzhe
 * @Date: 2020/11/27 23:07
 */

class DynamicDetailActivity : BaseViewModelActivity<DynamicDetailViewModel>() {

    companion object {
        fun activityStart(fragment: Fragment, dynamicItem: View, data: Dynamic) {
            fragment.apply {
                activity?.let {
                    val opt = ActivityOptionsCompat.makeSceneTransitionAnimation(it, dynamicItem, "dynamicItem")
                    val intent = Intent(context, DynamicDetailActivity::class.java)
                    intent.putExtra("dynamicItem", data)
                    it.window.exitTransition = Slide(Gravity.START).apply { duration = 500 }
                    startActivity(intent, opt.toBundle())
                }
            }
        }
    }

    private val emptyRvAdapter by lazy { EmptyRvAdapter(getString(R.string.qa_comment_list_empty_hint)) }

    private val footerRvAdapter = FooterRvAdapter { getCommentList }

    lateinit var dynamic: Dynamic

    private val behavior by lazy { AppBarLayout.ScrollingViewBehavior() }

    override val isFragmentActivity = false

    override fun getViewModelFactory() = DynamicDetailViewModel.Factory()

    private val getCommentList: () -> Unit = {
        viewModel.refreshCommentList(dynamic.postId, "0")
    }

    // 评论点击的逻辑
    private val commentListRvAdapter = CommentListAdapter(
            onItemClickEvent = { commentId ->
                KeyboardController.showInputKeyboard(this, qa_et_reply)
                viewModel.replyInfo.value = Pair("", commentId)
            },
            onReplyInnerClickEvent = { nickname, commentId ->
                KeyboardController.showInputKeyboard(this, qa_et_reply)
                viewModel.replyInfo.value = Pair(nickname, commentId)
            },
            onItemLongClickEvent = { comment, itemView ->
                // 消除回复弹窗
                viewModel.replyInfo.value = Pair("", "")

                val optionPopWindow = OptionalPopWindow.Builder().with(this)
                        .addOptionAndCallback(CommentConfig.REPLY) {
                            KeyboardController.showInputKeyboard(this, qa_et_reply)
                            viewModel.replyInfo.value = Pair(comment.nickName, comment.commentId)
                        }.addOptionAndCallback(CommentConfig.COPY) {
                            copyText(comment.content)
                            toast("已复制到剪切板")
                        }
                if (dynamic._isSelf == 1 || comment.isSelf) {
                    optionPopWindow.addOptionAndCallback(CommentConfig.DELETE) {
                        QaDialog.show(this, resources.getString(R.string.qa_dialog_tip_delete_comment_text), {}) {
                            toast("点击了删除")
                            viewModel.deleteId(comment.commentId, "1")
                        }
                    }
                } else {
                    optionPopWindow.addOptionAndCallback(CommentConfig.REPORT) {
                        QaReportDialog.show(this) {
                            toast("点击了举报")
                        }
                    }
                }
                optionPopWindow.show(itemView, OptionalPopWindow.AlignMode.CENTER, 0)

            },
            onReplyInnerLongClickEvent = { comment, itemView ->
                // 消除回复弹窗
                viewModel.replyInfo.value = Pair("", "")

                val optionPopWindow = OptionalPopWindow.Builder().with(this)
                        .addOptionAndCallback(CommentConfig.REPLY) {
                            KeyboardController.showInputKeyboard(this, qa_et_reply)
                            viewModel.replyInfo.value = Pair(comment.nickName, comment.commentId)
                        }.addOptionAndCallback(CommentConfig.COPY) {
                            copyText(comment.content)
                            toast("已复制到剪切板")
                        }
                if (dynamic._isSelf == 1 || comment.isSelf) {
                    optionPopWindow.addOptionAndCallback(CommentConfig.DELETE) {
                        QaDialog.show(this, resources.getString(R.string.qa_dialog_tip_delete_comment_text), {}) {
                            toast("点击了删除")
                            viewModel.deleteId(comment.commentId, "1")
                        }
                    }
                } else {
                    optionPopWindow.addOptionAndCallback(CommentConfig.REPORT) {
                        QaReportDialog.show(this) {
                            toast("点击了举报")
                        }
                    }
                }
                optionPopWindow.show(itemView, OptionalPopWindow.AlignMode.CENTER, 0)

            })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_dynamic_detail)
        window.enterTransition = Slide(Gravity.END).apply { duration = 500 }
        qa_tv_toolbar_title.text = resources.getText(R.string.qa_dynamic_detail_title_text)
        initObserve()
        initDynamic()
        initReplyList()


        qa_ib_toolbar_back.setOnSingleClickListener {
            onBackPressed()
        }

        qa_btn_send.setOnSingleClickListener {
            viewModel.releaseComment(dynamic.postId, qa_et_reply.text.toString())
        }
    }

    private fun initObserve() {
        viewModel.commentList.observe(this, Observer {
            if (it != null) {
                commentListRvAdapter.refreshData(it)
                qa_rv_comment_list.smoothScrollToPosition(viewModel.position)
                if (viewModel.position != 0) {
                    qa_appbar_dynamic.setExpanded(false, true)
                } else {
                    qa_appbar_dynamic.setExpanded(true, false)
                }
            }
        })


        viewModel.loadStatus.observe {
            it?.run {
                footerRvAdapter.refreshData(listOf(this))
            }
        }

        viewModel.loadStatus.observe {
            when (it) {
                NetworkState.LOADING -> {
                    qa_detail_swipe_refresh_layout.isRefreshing = true
                    emptyRvAdapter.showHolder(3)
                }
                NetworkState.CANNOT_LOAD_WITHOUT_LOGIN -> {
                    qa_detail_swipe_refresh_layout.isRefreshing = false
                }
                else -> {
                    qa_detail_swipe_refresh_layout.isRefreshing = false
                    emptyRvAdapter.hideHolder()
                }
            }
        }
        viewModel.replyInfo.observe {
            it?.apply {
                if (it.second.isNotEmpty()) {
                    ReplyPopWindow.with(this@DynamicDetailActivity)
                    ReplyPopWindow.setReplyName(it.first)
                    ReplyPopWindow.setOnClickEvent {
                        viewModel.replyInfo.value = Pair("", "")
                    }
                    ReplyPopWindow.show(qa_et_reply, ReplyPopWindow.AlignMode.LEFT, this@DynamicDetailActivity.dp2px(6f))
                } else {
                    if (ReplyPopWindow.isShowing()) {
                        ReplyPopWindow.dismiss()
                    }
                }

            }
        }
        viewModel.commentReleaseResult.observe {
            viewModel.replyInfo.value = Pair("", "")
            qa_et_reply.setText("")
            KeyboardController.hideInputKeyboard(this, qa_et_reply)

            viewModel.refreshCommentList(dynamic.postId, (it?.commentId
                    ?: 0).toString())

        }
    }

    override fun onBackPressed() {
        ReplyPopWindow.dismiss()
        window.returnTransition = Slide(Gravity.END).apply { duration = 500 }
        super.onBackPressed()
    }

    private fun initDynamic() {

        // 设置behavior
        val layoutParams = qa_ll_reply.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.behavior = behavior

        dynamic = intent.getParcelableExtra("dynamicItem")
        viewModel.dynamicLiveData.value = intent.getParcelableExtra("dynamicItem")
        viewModel.dynamicLiveData.observe {
            dynamic = it!!
        }

        qa_tv_reply_title.setOnClickListener {
            KeyboardController.showInputKeyboard(this, qa_et_reply)
        }


        qa_iv_dynamic_more_tips_clicked.setOnSingleClickListener {

            // 消除回复弹窗
            viewModel.replyInfo.value = Pair("", "")

            val optionPopWindow = OptionalPopWindow.Builder().with(this)
                    .addOptionAndCallback(CommentConfig.REPLY) {
                        KeyboardController.showInputKeyboard(this, qa_et_reply)
                        viewModel.replyInfo.value = Pair("", "")
                    }.addOptionAndCallback(CommentConfig.COPY) {
                        copyText(dynamic.content)
                        toast("已复制到剪切板")
                    }
            if (dynamic._isSelf == 1) {
                optionPopWindow.addOptionAndCallback(CommentConfig.DELETE) {
                    QaDialog.show(this, resources.getString(R.string.qa_dialog_tip_delete_comment_text), {}) {
                        toast("点击了删除")
                        viewModel.deleteId(dynamic.postId, "0")
                    }
                }
            } else {
                optionPopWindow.addOptionAndCallback(CommentConfig.REPORT) {
                    QaReportDialog.show(this) {
                        toast("点击了举报")
                    }
                }
            }
            optionPopWindow.show(it, OptionalPopWindow.AlignMode.RIGHT, 0)
        }
        qa_iv_dynamic_praise_count_image.registerLikeView(dynamic.postId, CommentConfig.PRAISE_MODEL_DYNAMIC, dynamic.isPraised, dynamic.praiseCount)
        qa_iv_dynamic_praise_count_image.setOnSingleClickListener {
            qa_iv_dynamic_praise_count_image.click()
        }
        qa_iv_dynamic_avatar.setAvatarImageFromUrl(dynamic.avatar)
        qa_tv_dynamic_topic.text = "#" + dynamic.topic
        qa_tv_dynamic_nickname.text = dynamic.nickName
        qa_tv_dynamic_content.text = dynamic.content
        qa_tv_dynamic_comment_count.text = dynamic.commentCount.toString()
        qa_tv_dynamic_publish_at.text = dynamicTimeDescription(System.currentTimeMillis(), dynamic.publishTime * 1000)
        if (dynamic.pics.isNullOrEmpty())
            qa_dynamic_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
        else {
            dynamic.pics.apply {
                val tag = qa_dynamic_nine_grid_view.tag
                if (null == tag || tag == this) {
                    val tagStore = qa_dynamic_nine_grid_view.tag
                    qa_dynamic_nine_grid_view.setImages(this, NineGridView.MODE_IMAGE_THREE_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                    qa_dynamic_nine_grid_view.tag = tagStore
                } else {
                    val tagStore = this
                    qa_dynamic_nine_grid_view.tag = null
                    qa_dynamic_nine_grid_view.setRectangleImages(emptyList(), NineGridView.MODE_IMAGE_THREE_SIZE)
                    qa_dynamic_nine_grid_view.setImages(this, NineGridView.MODE_IMAGE_NORMAL_SIZE, NineGridView.ImageMode.MODE_IMAGE_RECTANGLE)
                    qa_dynamic_nine_grid_view.tag = tagStore
                }
            }

        }
        qa_dynamic_nine_grid_view.setOnItemClickListener { _, index ->
            ViewImageActivity.activityStart(this, dynamic.pics.map { it }.toTypedArray(), index)
        }
    }


    private fun initReplyList() {

        qa_detail_swipe_refresh_layout.setOnRefreshListener {
            viewModel.commentList.value = listOf()
            getCommentList.invoke()
        }

        getCommentList.invoke()

        qa_rv_comment_list.apply {
            layoutManager = LinearLayoutManager(context)

            val adapterWrapper = RvAdapterWrapper(
                    normalAdapter = commentListRvAdapter,
                    emptyAdapter = emptyRvAdapter,
                    footerAdapter = footerRvAdapter
            )
            adapter = adapterWrapper
        }
    }


    private fun copyText(content: String) {
        val cm: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText("掌上重邮帖子内容", content)
        cm.primaryClip = mClipData
    }


}