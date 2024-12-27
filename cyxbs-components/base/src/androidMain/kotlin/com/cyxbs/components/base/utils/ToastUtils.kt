package com.cyxbs.components.base.utils

import android.widget.Toast
import androidx.annotation.StringRes
import com.cyxbs.components.utils.extensions.CyxbsToast
import com.cyxbs.components.utils.extensions.appContext

/**
 * Toast 工具类接口
 *
 * ## 为什么不放到 lib_utils 中？
 * 如果放到 lib_utils 中，在值依赖 lib_base 的时候会出现无法继承 BaseActivity 的情况
 *
 * 所以要求 base 类实现的接口尽量不要放在其他模块内
 */
interface ToastUtils {
  /**
   * 已自带处于其他线程时自动切换至主线程发送
   */
  fun toast(s: CharSequence?) {
    CyxbsToast.show(appContext, s, Toast.LENGTH_SHORT)
  }
  fun toastLong(s: CharSequence?) {
    CyxbsToast.show(appContext, s, Toast.LENGTH_LONG)
  }
  fun String.toast() = toast(this)
  fun String.toastLong() = toastLong(this)
  fun toast(@StringRes id: Int) = toast(appContext.getString(id))
}