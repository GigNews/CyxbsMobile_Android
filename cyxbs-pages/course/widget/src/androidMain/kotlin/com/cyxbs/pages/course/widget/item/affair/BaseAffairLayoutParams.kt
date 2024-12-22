package com.cyxbs.pages.course.widget.item.affair

import com.cyxbs.pages.course.widget.item.single.SingleDayLayoutParams
import com.ndhzs.netlayout.attrs.NetLayoutParams

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/19 14:05
 */
abstract class BaseAffairLayoutParams(
  weekNum: Int,
  startNode: Int,
  length: Int
) : SingleDayLayoutParams(
  weekNum, startNode, length
), IAffairData {
  
  constructor(data: IAffairData) : this(data.weekNum, data.startNode, data.length)
  
  /**
   * 如果你需要控制课表中 item 的显示顺序，你需要重写该方法
   *
   * 返回 1，则显示在上面
   */
  override fun compareTo(other: NetLayoutParams): Int {
    return super.compareTo(other)
  }
}