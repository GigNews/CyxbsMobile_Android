package com.cyxbs.pages.course.widget.helper.affair.expose

import com.cyxbs.pages.course.widget.fragment.page.ICoursePage
import com.cyxbs.pages.course.widget.helper.affair.CreateAffairDispatcher
import com.cyxbs.pages.course.widget.helper.affair.view.TouchAffairView
import com.cyxbs.pages.course.widget.internal.view.course.ICourseViewGroup
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent

/**
 * [CreateAffairDispatcher] 与外界进行交互的接口
 *
 * ## 该类作用
 * 深度定制 [CreateAffairDispatcher] 的一些行为，减少前者的代码耦合度
 *
 * @author 985892345
 * 2023/1/30 16:40
 */
interface ICreateAffairConfig : IBoundary {
  
  /**
   * 当前 Down 的触摸点是否合法，合法才会拦截触摸事件
   */
  fun isValidDown(page: ICoursePage, event: IPointerEvent): Boolean {
    val x = event.x.toInt()
    val y = event.y.toInt()
    if (x > page.getTimelineEndWidth()) {
      // 触摸位置大于左边时间轴的宽度时
      if (page.course.findItemUnderByXY(x, y) == null) {
        // 当前触摸的是空白位置时才准备拦截
        return true
      }
    }
    return false
  }
  
  /**
   * 创建 [ITouchAffairItem]，可以继承于 [TouchAffairView]
   */
  fun createTouchAffairItem(course: ICourseViewGroup, event: IPointerEvent): ITouchAffairItem {
    return TouchAffairView(course.getContext())
  }
  
  companion object Default : ICreateAffairConfig
}