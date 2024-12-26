package com.cyxbs.pages.mine.page.feedback.center.ui

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyxbs.pages.mine.base.ui.BaseMVPVMActivity
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.cyxbs.pages.mine.R
import com.cyxbs.pages.mine.databinding.MineActivityFeedbackCenterBinding
import com.cyxbs.pages.mine.page.feedback.center.adapter.FeedbackCenterAdapter
import com.cyxbs.pages.mine.page.feedback.center.presenter.FeedbackCenterPresenter
import com.cyxbs.pages.mine.page.feedback.center.viewmodel.FeedbackCenterViewModel
import com.cyxbs.pages.mine.page.feedback.edit.ui.FeedbackEditActivity
import com.cyxbs.pages.mine.page.feedback.history.list.HistoryListActivity
import com.mredrock.cyxbs.lib.utils.utils.Jump2QQHelper

/**
 * @Date : 2021/8/23   20:51
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
class FeedbackCenterActivity :
    BaseMVPVMActivity<FeedbackCenterViewModel, MineActivityFeedbackCenterBinding, FeedbackCenterPresenter>() {
    /**
     * 初始化adapter
     */
    private val mAdapter by lazy {
        FeedbackCenterAdapter()
    }

    /**
     * 获取P层
     */
    override fun createPresenter(): FeedbackCenterPresenter {
        return FeedbackCenterPresenter()
    }

    /**
     * 获取布局信息
     */
    override fun getLayoutId(): Int {
        return R.layout.mine_activity_feedback_center
    }

    /**
     * 初始化view
     */
    override fun initView() {
        mAdapter.setEventHandler(EventHandler())
        binding?.mineRecyclerview?.run {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }

    /**
     * 监听数据
     */
    override fun observeData() {
        viewModel.apply {
            contentList.observe{
                mAdapter.setData(it)
            }
        }
    }

    /**
     * 初始化listener
     */
    override fun initListener() {
        binding?.apply {
            fabCenterBack.setOnSingleClickListener {
                onBackPressed()
            }
            btnQuestion.setOnClickListener {
                startActivity(Intent(this@FeedbackCenterActivity, FeedbackEditActivity::class.java))
            }
            ivHistory.setOnSingleClickListener {
                startActivity(Intent(this@FeedbackCenterActivity, HistoryListActivity::class.java))
            }
            tvQqTwo.text = Jump2QQHelper.FEED_BACK_QQ_GROUP
            tvQqTwo.setOnSingleClickListener {
                Jump2QQHelper.onFeedBackClick()
            }
            fabCenterBack.setOnSingleClickListener { finish() }
        }
    }

    /**
     * 每个item的监听事件 通过dataBinding传递
     */
    inner class EventHandler {
        var position: Int = 0
        fun onItemClick(itemView: View, title: String, content: String) {
            val intent =
                Intent(this@FeedbackCenterActivity, FeedbackDetailActivity::class.java).apply {
                    putExtra("title", title)
                    putExtra("content", content)
                }
            startActivity(intent)
        }
    }
}