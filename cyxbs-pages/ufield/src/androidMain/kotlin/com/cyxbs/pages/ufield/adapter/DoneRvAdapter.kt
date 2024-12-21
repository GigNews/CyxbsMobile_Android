package com.cyxbs.pages.ufield.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.pages.ufield.R
import com.cyxbs.pages.ufield.bean.DoneBean
import com.cyxbs.pages.ufield.helper.timeFormat

/**
 *  author : lytMoon
 *  date : 2023/8/19 18:08
 *  description :负责展示已经审核过的活动Rv adapter
 *  version ： 1.0
 */
class DoneRvAdapter :
    ListAdapter<DoneBean, DoneRvAdapter.RvDoneViewHolder>(object :
        DiffUtil.ItemCallback<DoneBean>() {
        override fun areItemsTheSame(oldItem: DoneBean, newItem: DoneBean) = oldItem.activityId == newItem.activityId
        override fun areContentsTheSame(oldItem: DoneBean, newItem: DoneBean) = oldItem == newItem
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RvDoneViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.ufield_item_rv_done, parent, false)
    )

    override fun onBindViewHolder(holder: RvDoneViewHolder, position: Int) {
        val itemData = getItem(position)
        holder.bind(itemData)
    }


    /**
     * 点击按钮的回调
     */

    private var mClick: ((Int) -> Unit)? = null
    fun setOnItemClick(listener: (Int) -> Unit) {
        mClick = listener
    }


    inner class RvDoneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val actName: TextView = itemView.findViewById(R.id.uField_done_activity_name)
        private val actTime: TextView = itemView.findViewById(R.id.uField_done_activity_time)
        private val actAuthor: TextView = itemView.findViewById(R.id.uField_done_activity_author)
        private val actPhone: TextView = itemView.findViewById(R.id.uField_done_activity_phone)
        private val actImage: ImageView = itemView.findViewById(R.id.uField_todo_activity_isPass)


        init {
            itemView.setOnClickListener {
                mClick?.invoke(absoluteAdapterPosition)
            }
        }


        fun bind(itemData: DoneBean) {
            actName.text = itemData.activityTitle
            actTime.text = timeFormat(itemData.activityCreateTimestamp)
            actPhone.text = itemData.activityPhone
            actAuthor.text = itemData.activityCreator
            when (itemData.state) {
                "rejected" -> actImage.setImageResource(R.drawable.ufield_ic_reject)
                "published" -> actImage.setImageResource(R.drawable.ufield_ic_pass)
            }
        }

    }


}
