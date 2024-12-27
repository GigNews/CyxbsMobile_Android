package com.cyxbs.pages.noclass.page.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.cyxbs.pages.course.widget.helper.show.CourseNowTimeHelper
import com.cyxbs.pages.noclass.bean.NoClassSpareTime
import com.cyxbs.pages.noclass.page.viewmodel.other.CourseViewModel
import java.util.*

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.cyxbs.pages.noclass.page.ui.fragment
 * @ClassName:      NoClassSemesterFragment
 * @Author:         Yan
 * @CreateDate:     2022年09月06日 10:25:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    没课约显示整个学期的view
 */
class NoClassSemesterFragment : NoClassPageFragment() {
  
  companion object {
    fun newInstance(data: NoClassSpareTime): NoClassSemesterFragment {
      return NoClassSemesterFragment().apply {
        arguments = bundleOf(
          this::mNoClassSpareTime.name to data
        )
      }
    }
  }
  
  private val mParentViewModel by activityViewModels<CourseViewModel>()
  
  private val mNoClassSpareTime by arguments<NoClassSpareTime>()
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initCreateAffair()
    initToday()
    initObserve()
    addLessons(mNoClassSpareTime)
  }
  
  private fun initObserve(){
    mParentViewModel.noclassData.observe(viewLifecycleOwner){
      val mNoClassSpareTime = it[0]!!
      clearLesson()
      addLessons(mNoClassSpareTime)
    }
  }
  private fun initToday(){
    val calendar = Calendar.getInstance()
    val weekNum = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1
    /*
    * 星期天：1 -> 7
    * 星期一：2 -> 1
    * 星期二：3 -> 2
    * 星期三：4 -> 3
    * 星期四：5 -> 4
    * 星期五：6 -> 5
    * 星期六：7 -> 6
    *
    * 左边一栏是 Calendar.get(Calendar.DAY_OF_WEEK) 得到的数字，
    * 右边一栏是 weekNum 对应的数字
    * */
    showToday(weekNum)
  }
  
  override fun initTimeline() {
    super.initTimeline()
    CourseNowTimeHelper.attach(this)
  }


}