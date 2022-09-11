package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.page.adapter.NoClassGatheringAdapter

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui.dialog
 * @ClassName:      NoClassGatherDialog
 * @Author:         Yan
 * @CreateDate:     2022年09月10日 18:17:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */
class NoClassGatherDialog (
  private val mStuList : ArrayList<Pair<String,Boolean>>,
  private val mTextTime : String
) : BottomSheetDialogFragment() {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.noclass_sheet_dialog_style)
  }
  
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    super.onCreateDialog(savedInstanceState)
    val dialog = super.onCreateDialog(savedInstanceState)
    dialog.setContentView(R.layout.noclass_dialog_noclass_gather)
    initView(dialog)
    return dialog
  }
  private fun initView(dialog: Dialog){
    val mTvTotal = dialog.findViewById<TextView>(R.id.noclass_tv_gathering_total).apply {
      text = "人数：共计 ${mStuList.size} 人"
    }
    val mTvTime = dialog.findViewById<TextView>(R.id.noclass_tv_gathering_time).apply {
      text = mTextTime
    }
    val mViewPager2 = dialog.findViewById<ViewPager2>(R.id.noclass_vp_gather_container).apply {
      var index = 0
      //将学生列表进行拆分，list上每一个节点的map相当于一页上的八个学生
      val mStuListByPage = hashMapOf<Int,ArrayList<Pair<String,Boolean>>>()
      mStuList.forEach {
        if (mStuListByPage[index] == null){
          mStuListByPage[index] = arrayListOf()
        }
        mStuListByPage[index]!!.add(Pair(it.first,it.second))
        if (mStuListByPage[index]!!.size == 8){
          index++
        }
      }
      adapter = NoClassGatheringAdapter(context, mStuListByPage)
    } 
  }
  
  
}