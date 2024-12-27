package com.cyxbs.pages.ufield.ui.fragment.checkfragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.components.base.ui.BaseFragment
import com.cyxbs.pages.ufield.R
import com.cyxbs.pages.ufield.adapter.DoneRvAdapter
import com.cyxbs.pages.ufield.bean.DoneBean
import com.cyxbs.pages.ufield.ui.activity.DetailActivity
import com.cyxbs.pages.ufield.viewmodel.DoneViewModel
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * description ：已经审核过的活动
 * author : lytMoon
 * email : yytds@foxmail.com
 * date : 2023/8/7 19:49
 * version: 1.0
 */
class DoneFragment : BaseFragment() {

    private val mRv: RecyclerView by R.id.uField_done_rv.view()

    private val mViewModel by viewModels<DoneViewModel>()
    private val mAdapter: DoneRvAdapter by lazy { DoneRvAdapter() }
    private val mRefresh: SmartRefreshLayout by R.id.uField_check_refresh_done.view()
    private lateinit var mDataList: MutableList<DoneBean>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ufield_fragment_done, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniRv()
        iniRefresh()
    }

    /**
     * 展示已经处理过的活动数据
     */
    private fun iniRv() {

        mViewModel.apply {
            doneList.observe {
                mAdapter.submitList(it)
                mDataList = it as MutableList<DoneBean>
            }
        }
        mRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter.apply {
                setOnItemClick {
                    val intent = Intent(requireContext(), DetailActivity::class.java)
                    intent.putExtra("actID", mDataList[it].activityId)
                    startActivity(intent)
                }

            }
        }
    }

    /**
     * 处理刷新和加载
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun iniRefresh() {
        mRefresh.apply {
            setRefreshHeader(ClassicsHeader(requireContext()))
            setRefreshFooter(ClassicsFooter(requireContext()))
            setOnRefreshListener {
                mViewModel.apply {
                    getViewedData()
                }
                finishRefresh(600)
            }
            setOnLoadMoreListener {
                mViewModel.apply {
                    getViewedUpData(mDataList.lastOrNull()?.activityId ?: 1)
                }
                finishLoadMore(500)
            }
        }
    }
}