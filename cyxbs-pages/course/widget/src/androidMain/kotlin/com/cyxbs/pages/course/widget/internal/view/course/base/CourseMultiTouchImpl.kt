package com.cyxbs.pages.course.widget.internal.view.course.base

import android.content.Context
import android.util.AttributeSet
import com.cyxbs.pages.course.widget.R
import com.cyxbs.pages.course.widget.internal.touch.IMultiTouch
import com.ndhzs.netlayout.touch.multiple.IPointerDispatcher
import com.ndhzs.netlayout.touch.multiple.IPointerTouchHandler
import com.ndhzs.netlayout.touch.multiple.MultiTouchDispatcherHelper

/**
 * 实现多指触摸的父类
 *
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/18 12:39
 */
abstract class CourseMultiTouchImpl @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = R.attr.courseWidgetLayoutStyle,
  defStyleRes: Int = 0
) : CourseLayoutParamsHandler(context, attrs, defStyleAttr, defStyleRes), IMultiTouch {
  
  private val mMultiTouchDispatcherHelper = MultiTouchDispatcherHelper()
  
  final override fun addPointerDispatcher(dispatcher: IPointerDispatcher) {
    mMultiTouchDispatcherHelper.addPointerDispatcher(dispatcher)
  }
  
  final override fun setDefaultPointerDispatcher(dispatcher: IPointerDispatcher?) {
    mMultiTouchDispatcherHelper.setDefaultPointerDispatcher(dispatcher)
  }
  
  final override fun getDefaultPointerDispatcher(): IPointerDispatcher? {
    return mMultiTouchDispatcherHelper.getDefaultPointerDispatcher()
  }
  
  // 打上 final 修饰
  final override fun addOnAttachStateChangeListener(listener: OnAttachStateChangeListener) {
    super.addOnAttachStateChangeListener(listener)
  }
  
  final override fun removeOnAttachStateChangeListener(listener: OnAttachStateChangeListener) {
    super.removeOnAttachStateChangeListener(listener)
  }
  
  final override fun getTouchHandler(pointerId: Int): IPointerTouchHandler? {
    return mMultiTouchDispatcherHelper.getTouchHandler(pointerId)
  }
  
  init {
    addItemTouchListener(mMultiTouchDispatcherHelper)
  }
}