package com.cyxbs.pages.mine.util.ui

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cyxbs.components.utils.network.DownMessage
import com.cyxbs.pages.mine.R

/**
 * copy from UserAgreementAdapter
 */

class DynamicRVAdapter(private val list: List<DownMessage.DownMessageText>) :
    RecyclerView.Adapter<DynamicRVAdapter.ViewHolder>() {

    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val mine_about_rv_title:TextView = view.findViewById(R.id.mine_about_rv_title)
        val mine_about_rv_content:TextView = view.findViewById(R.id.mine_about_rv_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicRVAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.mine_list_item_feature_intro, parent, false)
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: DynamicRVAdapter.ViewHolder, position: Int) {
        holder.apply {
            restoredToTheirOriginal(mine_about_rv_title, mine_about_rv_content)
            when (list[position].title) {
                "title" -> {
                    mine_about_rv_title.apply {
                        textSize = 20f
                        gravity = Gravity.CENTER
                        text = list[position].content
                    }
                    mine_about_rv_content.visibility = View.GONE
                }
                "time" -> {
                    mine_about_rv_content.text = list[position].content
                    mine_about_rv_title.visibility = View.GONE
                }
                else -> {
                    //ß这个字符是我在下发文档里面嵌入的标识符，表示需要加上双tab
                    mine_about_rv_title.text = list[position].title.replace("ß", "      ")
                    mine_about_rv_content.text = list[position].content.replace("ß", "      ")
                    //后端返回数据出现问题，3.我的 这个DownMessageText item没有ß标识符，手动加上双tab
                    if (list[position].title == "3.我的") {
                        mine_about_rv_content.text =
                            list[position].content.replace("优化了", "        优化了")
                    }
                }
            }
        }
    }


    private fun restoredToTheirOriginal(title: TextView, content: TextView) {
        title.apply {
            gravity = Gravity.START
            textSize = 15f
            text = ""
            visibility = View.VISIBLE
        }
        content.apply {
            gravity = Gravity.START
            textSize = 15f
            text = ""
            visibility = View.VISIBLE
        }
    }
}