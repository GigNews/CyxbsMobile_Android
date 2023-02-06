package com.mredrock.cyxbs.lib.course.fragment.page

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.mredrock.cyxbs.lib.course.helper.show.CourseNowTimeHelper
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.config.config.SchoolCalendar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.*

/**
 * 每一周的页面模板
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 14:27
 */
abstract class CourseWeekFragment : CoursePageFragment() {
  
  /**
   * 建议写法：
   * ```
   * companion object {
   *   fun newInstance(week: Int): XXXWeekFragment {
   *     return XXXWeekFragment().apply {
   *       arguments = bundleOf(
   *         this::mWeek.name to week
   *       )
   *     }
   *   }
   * }
   *
   * override val mWeek by arguments<Int>()
   * ```
   */
  protected abstract val mWeek: Int
  
  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initWeekNum()
  }
  
  /**
   * 设置星期数
   * @return 今天是否在本周
   */
  protected open fun initWeekNum() {
    // 因为开学第一周的数据来自其他地方，存在此时没有被加载的情况，所以采用观察的形式
    SchoolCalendar.observeFirstMonDayOfTerm()
      .firstElement()
      .observeOn(AndroidSchedulers.mainThread())
      .safeSubscribeBy {
        it.add(Calendar.DATE, (mWeek - 1) * 7)
        val startTimestamp = it.timeInMillis
        setMonth(it)
        it.add(Calendar.DATE, 7)
        val isInThisWeek = System.currentTimeMillis() in startTimestamp .. it.timeInMillis
        onIsInThisWeek(isInThisWeek)
      }
  }
  
  protected open val mCourseNowTimeHelper by lazyUnlock {
    CourseNowTimeHelper.attach(this)
  }
  
  /**
   * 今天是否在本周内的回调
   * @param boolean 今天是否处于本周内
   */
  protected open fun onIsInThisWeek(boolean: Boolean) {
    mCourseNowTimeHelper.setVisible(boolean)
    if (boolean) {
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
  }
}