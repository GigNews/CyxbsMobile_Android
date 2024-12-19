package com.cyxbs.pages.declare.main.page.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.config.route.DECLARE_ENTRY
import com.cyxbs.pages.declare.databinding.DeclareActivityHomeBinding
import com.cyxbs.pages.declare.detail.page.activity.DetailActivity
import com.cyxbs.pages.declare.main.page.adapter.HomeRvAdapter
import com.cyxbs.pages.declare.main.page.viewmodel.HomeViewModel
import com.mredrock.cyxbs.lib.base.ui.BaseBindActivity
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.setOnDoubleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.visible

@Route(path = DECLARE_ENTRY)
class HomeActivity : BaseBindActivity<DeclareActivityHomeBinding>() {
    companion object {
        /**
         * 启动表态主页
         */
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }

    private val mViewModel by viewModels<HomeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val declareHomeRvAdapter = HomeRvAdapter()
        binding.run {
            declareHomeRecyclerview.run {
                layoutManager = LinearLayoutManager(this@HomeActivity)
                adapter = declareHomeRvAdapter
                declareHomeToolbarTv.setOnDoubleClickListener {
                    smoothScrollToPosition(0)
                }
            }
        }
        declareHomeRvAdapter.setOnItemClickedListener {
            DetailActivity.startActivity(this, it)
        }
        binding.declareHomeToolbarPost.setOnClickListener {
            //跳至自己发布过的话题页面
            PostedActivity.startActivity(this)
        }
        binding.declareIvToolbarArrowLeft.setOnClickListener {
            finish()
        }
        mViewModel.homeLiveData.observe {
            if (it.isEmpty()) {
                binding.declareHomeNoData.visible()
            } else {
                binding.declareHomeNoData.gone()
                declareHomeRvAdapter.submitList(it)
            }
        }
        mViewModel.homeErrorLiveData.observe {
            if (it) {
                binding.declareHomeCl.gone()
                binding.declareHomeNoNet.visible()
            } else {
                binding.declareHomeCl.visible()
                binding.declareHomeNoNet.gone()
            }
        }
        mViewModel.permLiveData.observe {
            binding.declareHomeToolbarPost.run {
                if (it.isPerm) visible() else gone()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.hasPerm()
        mViewModel.getHomeData()
    }
}