package com.mredrock.cyxbs.ufield.lxh.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.lib.utils.extensions.setImageFromId
import com.mredrock.cyxbs.lib.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.ufield.R
import com.mredrock.cyxbs.ufield.lxh.bean.RankBean

class RankAdapter : ListAdapter<RankBean, RankAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<RankBean>() {
        override fun areItemsTheSame(
            oldItem: RankBean,
            newItem: RankBean
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: RankBean,
            newItem: RankBean
        ): Boolean {
            return oldItem == newItem
        }
    }
) {
    private var rankPosition = 0

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val clClick: ConstraintLayout
        val numberImageView: ImageView
        val numberTextView: TextView
        val headImageView: ImageView
        val titleTextView: TextView
        val fireImageView: ImageView
        val fireTextView: TextView

        init {
            view.run {
                clClick = findViewById(R.id.ufield_activity_rank_cl_click)
                numberImageView = findViewById(R.id.ufield_activity_rank_iv_number)
                numberTextView = findViewById(R.id.ufield_activity_rank_tv_number)
                headImageView = findViewById(R.id.ufield_activity_rank_iv_head)
                titleTextView = findViewById(R.id.ufield_activity_rank_tv_title)
                fireImageView = findViewById(R.id.ufield_activity_rank_iv_fire)
                fireTextView = findViewById(R.id.ufield_activity_rank_tv_fire)
            }
            clClick.setOnSingleClickListener {
                getItemId(absoluteAdapterPosition).run {

                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ufield_activity_rank_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.run {
            if (position < 3) {
                numberTextView.visibility = View.GONE
                numberImageView.visibility = View.VISIBLE
            } else {
                numberImageView.visibility = View.GONE
                numberTextView.visibility = View.VISIBLE
                fireImageView.visibility = View.GONE
            }
            titleTextView.text = currentList[position].activityTitle
            headImageView.setImageFromUrl(currentList[position].activityCoverUrl)
            fireTextView.text = currentList[position].activityWatchNumber.toString()
            rankPosition += 1
            when (position) {
                0 -> {
                    numberImageView.setImageFromId(R.drawable.ufield_ic_activity_rank_number1)
                }

                1 -> {
                    numberImageView.setImageFromId(R.drawable.ufield_ic_activity_rank_number2)
                }

                2 -> {
                    numberImageView.setImageFromId(R.drawable.ufield_ic_activity_rank_number3)
                }

                else -> {
                    numberTextView.text = rankPosition.toString()
                }
            }
        }
    }
}