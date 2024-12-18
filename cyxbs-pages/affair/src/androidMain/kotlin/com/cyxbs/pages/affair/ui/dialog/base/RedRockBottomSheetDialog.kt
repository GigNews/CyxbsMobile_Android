package com.cyxbs.pages.affair.ui.dialog.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.cyxbs.pages.affair.R

/**
 * author: WhiteNight(1448375249@qq.com)
 * date: 2022/9/7
 * description:
 */
abstract class RedRockBottomSheetDialog(context: Context) :
  BottomSheetDialog(context, R.style.BottomSheetDialogTheme) {
  override fun setContentView(view: View) {
    val viewGroup = LayoutInflater.from(context).inflate(
      R.layout.affair_dialog,
      window?.decorView as ViewGroup,
      false
    ) as FrameLayout
    viewGroup.addView(view)
    super.setContentView(viewGroup)
  }
}