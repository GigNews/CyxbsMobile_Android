package com.cyxbs.pages.course.widget.fragment.course

import com.cyxbs.pages.course.widget.fragment.course.expose.container.ICourseContainer
import com.cyxbs.pages.course.widget.fragment.course.expose.fold.IFold
import com.cyxbs.pages.course.widget.fragment.course.expose.overlap.IOverlapContainer
import com.cyxbs.pages.course.widget.fragment.course.expose.period.ICoursePeriod
import com.cyxbs.pages.course.widget.fragment.course.expose.wrapper.ICourseWrapper
import com.cyxbs.pages.course.widget.fragment.course.expose.wrapper.IScrollWrapper
import com.cyxbs.pages.course.widget.fragment.course.expose.timeline.ITimeline

/**
 * course 包下主要放只跟课表这个容器相关的实现类
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/31 15:43
 */
interface ICourseBase :
  ICourseWrapper, // 包裹 CourseViewGroup
  IScrollWrapper, // 包裹 CourseScroll
  ICourseContainer, // 课表容器
  IFold, // 折叠中午或傍晚时间段
  IOverlapContainer, // 用于实现重叠的逻辑
  ICoursePeriod, // 课表时间区域
  ITimeline // 左侧时间轴
{
}