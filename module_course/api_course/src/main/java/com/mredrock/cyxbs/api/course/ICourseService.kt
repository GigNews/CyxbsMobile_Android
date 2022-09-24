package com.mredrock.cyxbs.api.course

import android.content.Context
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/2/9 16:39
 */
interface ICourseService : IProvider {
  
  /**
   * 尝试调用 replace() 显示主页的课表，如果已经添加，则不会重复添加
   *
   * 内部按规范添加 Fragment，对于异常重启，不会生成新的 Fragment
   *
   * @param fm FragmentManager，内部自动帮你添加课表 Fragment
   */
  fun tryReplaceHomeCourseFragmentById(
    fm: FragmentManager,
    @IdRes
    id: Int
  )
  
  /**
   * 设置课表头的透明度
   */
  fun setHeaderAlpha(alpha: Float)
  
  /**
   * 设置课表 Vp 页面的透明度
   */
  fun setCourseVpAlpha(alpha: Float)
  
  /**
   * 设置 BottomSheet 偏移量
   */
  fun setBottomSheetSlideOffset(offset: Float)
  
  /**
   * 通过 [lesson] 打开对应 BottomSheetDialog
   */
  fun openBottomSheetDialogByLesson(context: Context, lesson: ILessonService.Lesson)
  
  /**
   * 通过 [affair] 打开对应 BottomSheetDialog
   */
  fun openBottomSheetDialogByAffair(context: Context, affair: Any)
}