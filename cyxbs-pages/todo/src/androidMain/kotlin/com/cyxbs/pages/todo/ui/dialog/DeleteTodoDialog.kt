package com.cyxbs.pages.todo.ui.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.cyxbs.pages.todo.R
import com.cyxbs.pages.todo.util.getColor
import com.cyxbs.components.base.dailog.ChooseDialog
import com.cyxbs.components.utils.extensions.dp2px

/**
 * @Project: CyxbsMobile_Android
 * @File: DelteTodoDialog
 * @Author: 86199
 * @Date: 2024/8/24
 * @Description: 删除有时的弹窗
 */
class DeleteTodoDialog  private constructor(
    context: Context,
) : ChooseDialog(context) {
    class Builder(context: Context) : ChooseDialog.Builder(
        context,
        DataImpl(
            buttonWidth = 92,
            buttonHeight = 36,
        )
    ) {
        override fun buildInternal(): DeleteTodoDialog {
            return DeleteTodoDialog(context)
        }
    }

    // 内容
    private val mTvContent = TextView(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,//宽度
            LinearLayout.LayoutParams.WRAP_CONTENT //高度
        ).apply {
            topMargin = 5.dp2px
            bottomMargin = 31.dp2px
        }
        setTextColor(getColor(R.color.todo_check_line_color))
        textSize = 15F
        gravity = Gravity.CENTER //居中显示
    }

    override fun createContentView(parent: FrameLayout): View {
        return LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            addView(
                // 标题
                TextView(parent.context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 31.dp2px
                    }
                    setTextColor(getColor(R.color.todo_check_line_color))
                    textSize = 15F
                    gravity = Gravity.CENTER
                    text = "确定是否删除"
                }
            )
            addView(mTvContent)
        }
    }

    override fun initContentView(view: View) {
        mTvContent.text = "删除后无法恢复"
    }
}