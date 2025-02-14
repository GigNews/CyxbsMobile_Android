package com.cyxbs.pages.todo.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.aigestudio.wheelpicker.WheelPicker
import com.cyxbs.pages.todo.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.cyxbs.components.utils.extensions.gone
import com.cyxbs.components.utils.extensions.visible
import java.util.Calendar

/**
 * description: 选择时间的Dialog
 * author: sanhuzhen
 * date: 2024/8/12 19:48
 */
class TimeSelectDialog(
    context: Context,
    style: Int,
    val selectedYear: Int,
    val selectedMonth: Int,
    val selectedDay: Int,
    val lineVisibility: Int,
    val onTimeSelected: (Int, Int) -> Unit
) : BottomSheetDialog(context, style) {


    private lateinit var btCancel: AppCompatTextView
    private lateinit var btConfirm: AppCompatTextView
    private lateinit var hourWheelPicker: WheelPicker
    private lateinit var minuteWheelPicker: WheelPicker
    private lateinit var timeLine: View

    init {
        val dialogView =
            LayoutInflater.from(context)
                .inflate(R.layout.todo_dialog_bottom_sheet_timeselector, null, false)
        setContentView(dialogView)
        initView(this)
        dialogView?.apply {
            if (lineVisibility == 1) timeLine.visible()
            else timeLine.gone()
            setupWheelPickers()
            initClick()
        }
    }

    private fun initView(dialog: Dialog) {
        btCancel = dialog.findViewById(R.id.todo_bt_cancel_timeselector)
        btConfirm = dialog.findViewById(R.id.todo_bt_confirm_timeselector)
        hourWheelPicker = dialog.findViewById(R.id.todo_wheelpicker_hour_timeselector)
        minuteWheelPicker = dialog.findViewById(R.id.todo_wheelpicker_minute_timeselector)
        timeLine = dialog.findViewById(R.id.todo_view_time_selector_line)
    }

    private fun initClick() {
        btCancel.setOnClickListener {
            dismiss()
        }

        btConfirm.setOnClickListener {
            val selectedHour = hourWheelPicker.data?.get(hourWheelPicker.currentItemPosition) as Int
            val selectedMinute =
                minuteWheelPicker.data?.get(minuteWheelPicker.currentItemPosition) as Int
            onTimeSelected(selectedHour, selectedMinute)
            dismiss()
        }
    }

    private fun setupWheelPickers() {
        val calendar = Calendar.getInstance()
        var startTimeHour = 0
        var startTimeMinute = 0

        if (isToday(selectedYear, selectedMonth, selectedDay)) {
            startTimeHour = calendar.get(Calendar.HOUR_OF_DAY)
            startTimeMinute = calendar.get(Calendar.MINUTE)
        }

        // 设置小时选择器的值范围
        hourWheelPicker.data = (startTimeHour..23).toList()
        // 设置分钟选择器的值范围
        minuteWheelPicker.data = (startTimeMinute..59).toList()
        hourWheelPicker.setOnItemSelectedListener { _, name, _ ->
            when (name) {
                startTimeHour -> {
                    minuteWheelPicker.data = (startTimeMinute..59).toList()
                }

                else -> {
                    minuteWheelPicker.data = (0..59).toList()
                    minuteWheelPicker.isCyclic = true
                }
            }
        }

        // 如果时间选择的范围太小，取消滚动
        hourWheelPicker.isCyclic = (hourWheelPicker.data?.size ?: 0) > 7
        minuteWheelPicker.isCyclic = (minuteWheelPicker.data?.size ?: 0) > 7

        // 设置默认值为当天时间或初始值
        hourWheelPicker.selectedItemPosition = 0
        minuteWheelPicker.selectedItemPosition = 0
    }

    // 判断是否是今天
    private fun isToday(year: Int, month: Int, day: Int): Boolean {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR) == year &&
                calendar.get(Calendar.MONTH) == month &&
                calendar.get(Calendar.DAY_OF_MONTH) == day
    }

}