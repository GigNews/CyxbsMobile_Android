package com.cyxbs.pages.volunteer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.pages.volunteer.R
import com.cyxbs.pages.volunteer.bean.VolunteerAffair
import com.cyxbs.pages.volunteer.utils.DateUtils.lastDay
import com.cyxbs.pages.volunteer.utils.DateUtils.stamp2Date

/**
 * Created by yyfbe, Date on 2020/9/4.
 */
class VolunteerAffairAdapter(
    private var dataList: List<VolunteerAffair>,
    private val onItemClick: (VolunteerAffair) -> Unit
) : RecyclerView.Adapter<VolunteerAffairAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_volunteer_affair_title: TextView
        var tv_volunteer_affair_reward: TextView
        var tv_volunteer_affair_start_time: TextView
        var tv_volunteer_affair_sign_up_end: TextView

        init {
            tv_volunteer_affair_title = view.findViewById<TextView>(R.id.tv_volunteer_affair_title)
            tv_volunteer_affair_reward =
                view.findViewById<TextView>(R.id.tv_volunteer_affair_reward)
            tv_volunteer_affair_start_time =
                view.findViewById<TextView>(R.id.tv_volunteer_affair_start_time)
            tv_volunteer_affair_sign_up_end =
                view.findViewById<TextView>(R.id.tv_volunteer_affair_sign_up_end)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VolunteerAffairAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.volunteer_item_volunteer_affair, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: VolunteerAffairAdapter.ViewHolder, position: Int) {
        holder.apply {
            tv_volunteer_affair_title.text = dataList[position].name
            tv_volunteer_affair_reward.text =
                itemView.context.getString(R.string.volunteer_value, dataList[position].hour)
            tv_volunteer_affair_start_time.text = itemView.context.getString(
                R.string.volunteer_server_time,
                stamp2Date(dataList[position].date)
            )
            tv_volunteer_affair_sign_up_end.apply {
                val last = lastDay(dataList[position].lastDate)
                text = holder.itemView.context.getString(
                    R.string.volunteer_sign_up_end_time,
                    last.toString()
                )
                if (last <= 3) {
                    //三天之后结束，更换颜色
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            com.mredrock.cyxbs.common.R.color.common_volunteer_affair_sign_up_end_t0
                        )
                    )
                } else {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            com.mredrock.cyxbs.common.R.color.common_volunteer_affair_sign_up_end_t1
                        )
                    )
                }
            }
        }
        holder.itemView.setOnClickListener {
            onItemClick(dataList[position])
        }
    }

    fun refreshData(newList: List<VolunteerAffair>) {
        this.dataList = newList
        this.notifyDataSetChanged()
    }

}