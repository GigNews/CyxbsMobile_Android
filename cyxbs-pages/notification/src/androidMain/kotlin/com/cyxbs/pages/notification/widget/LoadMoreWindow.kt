package com.cyxbs.pages.notification.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.PopupWindow
import androidx.annotation.LayoutRes
import com.cyxbs.pages.notification.R
import com.cyxbs.pages.notification.util.changeWindowAlpha
import com.cyxbs.components.utils.extensions.setOnSingleClickListener

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 16:10.
 *
 */
@SuppressLint("InflateParams")
class LoadMoreWindow(
    private val context: Context,
    @LayoutRes
    private val layoutRes: Int,
    private val window: Window,
    Width: Int,
    Height: Int
) : PopupWindow() {

    init {
        window.changeWindowAlpha(0.7f)
        contentView = LayoutInflater.from(context).inflate(layoutRes, null)
        isFocusable = true
        animationStyle = R.style.notification_mypopwindow_anim_style
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        width = Width
        height = Height
    }

    fun setOnItemClickListener(itemViewRes: Int, clickEvent: (() -> Unit)) {
        contentView.findViewById<View>(itemViewRes).setOnSingleClickListener {
            clickEvent.invoke()
            dismiss()
        }
    }

    override fun dismiss() {
        super.dismiss()
        window.changeWindowAlpha(1.0F)
    }
}