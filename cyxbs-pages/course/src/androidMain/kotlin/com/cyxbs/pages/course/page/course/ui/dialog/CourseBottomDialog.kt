package com.cyxbs.pages.course.page.course.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.cyxbs.pages.course.R
import com.cyxbs.pages.course.page.course.data.ICourseItemData
import com.cyxbs.pages.course.page.course.ui.dialog.adapter.DialogBottomVpAdapter
import com.cyxbs.components.utils.extensions.gone
import com.ndhzs.slideshow.SlideShow

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/16 10:38
 */

/**
 * @param data 重叠的数据
 * @param isShowLink 是否显示关联人图标 (如果数据中的学号不是自己时，会显示关联图标)
 */
class CourseBottomDialog(
  context: Context,
  val data: List<ICourseItemData>,
  val isShowLink: Boolean
) : BottomSheetDialog(context, com.cyxbs.components.config.R.style.ConfigBottomSheetDialogTheme) {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.course_diolog_bottom)
    initView()
  }
  
  private fun initView() {
    val slideShow = findViewById<SlideShow>(R.id.course_ss_dialog_bottom)
    if (data.size > 1) {
      // 大于一个时开启循环
      slideShow.setIsCyclical(true)
    } else {
      // 只有一个时隐藏指示器
      findViewById<View>(R.id.course_indicator_dialog_bottom).gone()
    }
    slideShow.setAdapter(DialogBottomVpAdapter(this, data, isShowLink))
  }
  
  override fun <T : View> findViewById(id: Int): T {
    return super.findViewById(id)!!
  }
}