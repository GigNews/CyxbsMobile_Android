package com.mredrock.cyxbs.ufield.lxh.adapter

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.gyd.ui.DetailActivity
import com.mredrock.cyxbs.ufield.lxh.bean.DetailMsg
import com.mredrock.cyxbs.ufield.lxh.util.formatNumberToTime

class MsgAdapter : ListAdapter<DetailMsg, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<DetailMsg>() {
        override fun areItemsTheSame(
            oldItem: DetailMsg,
            newItem: DetailMsg
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DetailMsg,
            newItem: DetailMsg
        ): Boolean {
            return oldItem == newItem
        }
    }
) {
    companion object {
        const val TYPE_ONE = 1
        const val TYPE_SECOND = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_ONE
        else TYPE_SECOND
    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    inner class BlankHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mtClick: CardView
        val headImageView: ImageView
        val title: TextView
        val detail: TextView
        val time: TextView

        init {
            view.run {
                mtClick = findViewById(R.id.ufield_fragment_mc_click)
                headImageView = findViewById(R.id.ufield_fragment_iv_head)
                title = findViewById(R.id.ufield_fragment_tv_title)
                detail = findViewById(R.id.ufield_fragment_tv_detail)
                time = findViewById(R.id.ufield_fragment_tv_time)
            }
            mtClick.setOnSingleClickListener {
                getItemId(absoluteAdapterPosition).run {
                    getItemId(absoluteAdapterPosition).run {
                        val intent = Intent(view.context, DetailActivity::class.java)
                        intent.putExtra("actID", currentList[absoluteAdapterPosition].activityId)
                        startActivity(view.context, intent, null)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var holder: RecyclerView.ViewHolder? = null


        when (viewType) {
            TYPE_ONE -> {
                holder = BlankHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.ufield_fragment_campaign_blank, parent, false)
                )
            }

            TYPE_SECOND -> {
                holder = ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.ufield_activity_campaign_item, parent, false)
                )
            }
        }

        return holder!!
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.run {
                headImageView.setImageFromUrl(currentList[position-1].activityCoverUrl)
                title.text = currentList[position-1].activityTitle
                detail.text = currentList[position-1].activityDetail
                time.text = formatNumberToTime(currentList[position-1].activityEndAt)
            }
        }
    }
}