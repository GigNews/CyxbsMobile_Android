package com.mredrock.cyxbs.noclass.page.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoClassGroup
import com.mredrock.cyxbs.noclass.widget.SlideMenuLayout


/**
 * 没课约固定分组界面的adapter
 */
class NoClassSolidAdapter : ListAdapter<NoClassGroup, NoClassSolidAdapter.MyHolder>(solidDiffUtil) {
    companion object {
        private val solidDiffUtil: DiffUtil.ItemCallback<NoClassGroup> =
            object : DiffUtil.ItemCallback<NoClassGroup>() {
                override fun areItemsTheSame(
                    oldItem: NoClassGroup,
                    newItem: NoClassGroup
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: NoClassGroup,
                    newItem: NoClassGroup
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    //分别是点击名称，置顶，和删除的回调
    private var onClickGroupName : ((noclassGroup: NoClassGroup) -> Unit)? = null
    private var onClickGroupIsTop : ((noclassGroup: NoClassGroup, tvIsGroup : TextView) -> Unit)? = null
    private var onClickGroupDelete : ((noclassGroup: NoClassGroup) -> Unit)? = null

    fun setOnClickGroupName(onClickGroupName : (noclassGroup: NoClassGroup) -> Unit){
        this.onClickGroupName = onClickGroupName
    }

    fun setOnClickGroupIsTop(onClickGroupIsTop : (noclassGroup: NoClassGroup, tvIsGroup : TextView) -> Unit){
        this.onClickGroupIsTop = onClickGroupIsTop
    }

    fun setOnClickGroupDelete(onClickGroupDelete : (noclassGroup: NoClassGroup) -> Unit){
        this.onClickGroupDelete = onClickGroupDelete
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGroupName: TextView = itemView.findViewById(R.id.tv_noclass_group_name)
        val tvGroupIsTop: TextView = itemView.findViewById(R.id.tv_noclass_group_top_name)
        private val tvGroupDelete: TextView = itemView.findViewById(R.id.tv_noclass_group_delete_item)
        private val mMenuLayout : SlideMenuLayout = itemView.findViewById(R.id.slide_noclass_container)
        init {
            tvGroupIsTop.text = "取消置顶"
            tvGroupName.setOnClickListener {
                val item = getItem(bindingAdapterPosition)
                onClickGroupName?.invoke(item)
            }
            tvGroupIsTop.setOnClickListener {
                val item = getItem(bindingAdapterPosition)
                onClickGroupIsTop?.invoke(item,tvGroupIsTop)
                mMenuLayout.closeRightSlide()
            }
            tvGroupDelete.setOnClickListener {
                val item = getItem(bindingAdapterPosition)
                onClickGroupDelete?.invoke(item)
                mMenuLayout.closeRightSlide()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.noclass_item_group_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val item = getItem(position)
        holder.apply {
            tvGroupName.text = item.name
            tvGroupIsTop.text = if(item.isTop) "取消置顶" else "置顶"
        }
    }
    /**
     * 将传进来的列表变成有序列表，上面是置顶
     */
    fun submitListToOrder(noclassGroups: List<NoClassGroup>){
        val orderList = ArrayList<NoClassGroup>()
        noclassGroups.forEach {
            if (it.isTop){
                orderList.add(0,it)
            }else{
                orderList.add(it)
            }
        }
        submitList(orderList)
    }

    fun deleteGroup(noclassGroup: NoClassGroup){
        val list = currentList.toMutableList()
        list.remove(noclassGroup)
        submitList(list)
    }

    /**
     * 远端更新成功之后就调用，将该条数据按照是否有序插入并且提交，相比于submitListToOrder减少了比较
     */
    fun addItemToOrder(item : NoClassGroup, tvIsGroup : TextView){
        val list = currentList.toMutableList()
        if (item.isTop){
            tvIsGroup.text = "置顶"
            list.remove(item)
            //取消置顶就放到最后
            list.add(NoClassGroup(item.id,false,item.members,item.name))  //置顶效果
        }else{
            tvIsGroup.text = "取消置顶"
            list.remove(item)
            //置顶就放到第一个
            list.add(0, NoClassGroup(item.id,true,item.members,item.name))  //置顶效果
        }
        submitList(list)
    }
}