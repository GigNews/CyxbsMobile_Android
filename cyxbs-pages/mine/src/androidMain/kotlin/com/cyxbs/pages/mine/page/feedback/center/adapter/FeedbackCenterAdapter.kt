package com.cyxbs.pages.mine.page.feedback.center.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.pages.mine.R
import com.cyxbs.pages.mine.databinding.MineItemQuestionBinding
import com.cyxbs.pages.mine.page.feedback.center.ui.FeedbackCenterActivity
import com.cyxbs.pages.mine.page.feedback.network.bean.NormalFeedback

/**
 * @Date : 2021/8/24   18:27
 * @By ysh
 * @Usage :
 * @Request : God bless my code
 **/
class FeedbackCenterAdapter : RecyclerView.Adapter<FeedbackCenterAdapter.InnerViewHolder>() {

    private var eventHandler:FeedbackCenterActivity.EventHandler?=null

    private val mContentList by lazy {
        mutableListOf<NormalFeedback.Data>()
    }

    inner class InnerViewHolder(itemView:View,val itemBinding:MineItemQuestionBinding):RecyclerView.ViewHolder(itemView){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val itemBinding = DataBindingUtil.inflate<MineItemQuestionBinding>(
                LayoutInflater.from(parent.context),
                R.layout.mine_item_question,
                parent,
                false
        )
        return InnerViewHolder(itemBinding.root,itemBinding)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        holder.itemBinding.eventHandler = eventHandler
        holder.itemBinding.data = mContentList[position]
    }

    override fun getItemCount(): Int {
        return mContentList.size
    }

    fun setData(contentList:List<NormalFeedback.Data>){
        mContentList.clear()
        mContentList.addAll(contentList)

        notifyDataSetChanged()
    }

    fun setEventHandler(eventHandler:FeedbackCenterActivity.EventHandler){
        this.eventHandler = eventHandler
    }
}