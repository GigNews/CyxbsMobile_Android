package com.cyxbs.pages.course.widget.fragment.vp.expose

import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cyxbs.pages.course.widget.fragment.page.CoursePageFragment

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 13:28
 */
interface ICourseVp {
  
  /**
   * 课表页面数量
   */
  val mPageCount: Int
  
  /**
   * 每一页的 [CoursePageFragment]
   */
  fun createFragment(position: Int): CoursePageFragment
  
  /**
   * 课表 vp
   */
  val mViewPager: ViewPager2
  
  /**
   * 课表的 Adapter，只允许使用 [FragmentStateAdapter]
   */
  val mVpAdapter: FragmentStateAdapter
    get() = mViewPager.adapter as FragmentStateAdapter
  
  /**
   * 当前周数
   *
   * ## 注意
   * - 可能为负数，请做好越界处理。建议使用下面的写法
   * ```
   * if (mNowWeek >= mVpAdapter.itemCount) 0 else max(mNowWeek, 0)
   * ```
   */
  val mNowWeek: Int
}