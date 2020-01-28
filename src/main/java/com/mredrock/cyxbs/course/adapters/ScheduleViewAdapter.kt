package com.mredrock.cyxbs.course.adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.config.DEFAULT_PREFERENCE_FILENAME
import com.mredrock.cyxbs.common.config.SP_SHOW_MODE
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.SchoolCalendar
import com.mredrock.cyxbs.common.utils.extensions.sharedPreferences
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.component.ScheduleView
import com.mredrock.cyxbs.course.component.TestView
import com.mredrock.cyxbs.course.event.DismissAddAffairViewEvent
import com.mredrock.cyxbs.course.network.Course
import com.mredrock.cyxbs.course.ui.EditAffairActivity
import com.mredrock.cyxbs.course.ui.ScheduleDetailDialogHelper
import com.mredrock.cyxbs.course.utils.ClassRoomParse
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.textColor
import java.util.*

/**
 * @param mContext [Context]
 * @param mNowWeek 表示当前的周数
 * @param mSchedules 表示显示的数据
 * @param mIsBanTouchView 是否禁用在空白处的点击
 *
 * Created by anriku on 2018/8/14.
 */
class ScheduleViewAdapter(private val mContext: Context,
                          private val mNowWeek: Int,
                          private val mSchedules: List<Course>,
                          private val mIsBanTouchView: Boolean) :
        ScheduleView.Adapter() {

    companion object {
        private const val TAG = "ScheduleViewAdapter"
        private const val NOT_LONG_COURSE = -1
    }

    private var mInflater: LayoutInflater = LayoutInflater.from(mContext)
    private val mShowModel = mContext.sharedPreferences(DEFAULT_PREFERENCE_FILENAME).getBoolean(SP_SHOW_MODE, true)
    private val mSchedulesArray = Array(6) { arrayOfNulls<MutableList<Course>>(7) }

    private val mCoursesColors by lazy(LazyThreadSafetyMode.NONE) {
        intArrayOf(ContextCompat.getColor(mContext, R.color.morningCourseColor),
                ContextCompat.getColor(mContext, R.color.afternoonCourseColor),
                ContextCompat.getColor(mContext, R.color.eveningCourseColor),
                ContextCompat.getColor(mContext, R.color.courseCoursesOther))
    }
    private val mCoursesTextColors by lazy(LazyThreadSafetyMode.NONE) {
        intArrayOf(ContextCompat.getColor(mContext, R.color.morningCourseTextColor),
                ContextCompat.getColor(mContext, R.color.afternoonCourseTextColor),
                ContextCompat.getColor(mContext, R.color.eveningCourseTextColor))
    }

    private val mDialogHelper: ScheduleDetailDialogHelper by lazy(LazyThreadSafetyMode.NONE) {
        ScheduleDetailDialogHelper(mContext)
    }

    private lateinit var mTop: TextView
    private lateinit var mBottom: TextView
    private lateinit var mBackground: View
    private lateinit var mTag: View
    private lateinit var mAffairBackground: TestView


    init {
        addCourse()
        sortCourse()
    }

    /**
     * 这个方法用于进行课程的添加
     */
    private fun addCourse() {

        //下方复用代码，忽视就好
        fun initSchedulesArray(row: Int, column: Int) {
            if (mSchedulesArray[row][column] == null) {
                mSchedulesArray[row][column] = mutableListOf()
            }
        }

        for (course in mSchedules) {
            val row = course.hashLesson
            val column = course.hashDay

            //如果是整学期将所有数据返回
            if (mNowWeek == 0) {
                if (course.customType == Course.COURSE) {
                    initSchedulesArray(row, column)
                    mSchedulesArray[row][column]?.add(course)
                }
                continue
            }
            //如果不是整学期的做如下判断
            course.week?.let {
                //如果本周在这个课所在的周数list当中
                if (mNowWeek in it) {
                    initSchedulesArray(row, column)
                    mSchedulesArray[row][column]?.add(course)
                }
            }
        }
    }

    /**
     * 这个方法用于对课表进行排序
     */
    private fun sortCourse() {
        for (row in 0..5) {
            for (column in 0..6) {
                if (mSchedulesArray[row][column]?.size ?: 0 > 1) {
                    val courses = mSchedulesArray[row][column]
                    Collections.sort(courses, CourseComparator)
                    LogUtils.d(TAG, courses.toString())
                }
            }
        }
    }

    /**
     * 如果[mIsBanTouchView]为true禁止mTouchView；反之就返回添加事务的事件。
     */
    override fun setOnTouchViewClickListener(): ((ImageView) -> Unit)? {
        if (mIsBanTouchView) {
            return null
        } else {
            return { touchView ->
                touchView.setOnClickListener {
                    mContext.startActivity(Intent(mContext, EditAffairActivity::class.java).apply {
                        putExtra(EditAffairActivity.WEEK_NUM, mNowWeek)
                        putExtra(EditAffairActivity.TIME_NUM, (touchView.tag ?: 0) as Int)
                    })
                }
            }
        }
    }

    /**
     * 在ScheduleView中通过getItemViewInfo方法获取当前行列有schedule信息后，才会调用此方法
     * @param row 行
     * @param column 列
     * @param container [ScheduleView]
     * @return 添加的View
     */
    override fun getItemView(row: Int, column: Int, container: ViewGroup): View {
        val itemView = mInflater.inflate(R.layout.course_schedule_item_view, container, false)
        //itemInfo表示当前行列的第一个schedule的信息，itemCount表示当前行列schedule的数量
        val itemViewInfo = getItemViewInfo(row, column)
        var itemCount = 1
        mSchedulesArray[row][column]?.let {
            if (row == 0 || row == 2 || row == 4) {
                mSchedulesArray[row+1][column]
            }
            itemCount = it.size
            setItemViewOnclickListener(itemView, it)
        }

        mTop = itemView.findViewById(R.id.top)
        mBottom = itemView.findViewById(R.id.bottom)
        mBackground = itemView.findViewById(R.id.background)
        mTag = itemView.findViewById(R.id.tag)
        mAffairBackground = itemView.findViewById(R.id.affair_item_background)



        itemViewInfo?.let {
            when {
                row <= 1 -> {
                    setItemView(mSchedulesArray[row][column]!![0], 0, itemCount)
                }
                row <= 3 -> {
                    setItemView(mSchedulesArray[row][column]!![0], 1, itemCount)
                }
                row <= 5 -> {
                    setItemView(mSchedulesArray[row][column]!![0], 2, itemCount)
                }
            }
        }

        return itemView
    }

    /**
     * 此方法用于对即将添加的ItemView进行数据设置
     *
     * @param course Course相关信息
     * @param index 表示取那个颜色
     * @param itemCount 表示该位置Course的数量
     */
    private fun setItemView(course: Course, index: Int, itemCount: Int) {
        val top = mTop
        val bottom = mBottom
        val tag = mTag
        val background = mBackground

        if (course.customType == Course.COURSE) {
            top.text = course.course
            bottom.text = ClassRoomParse.parseClassRoom(course.classroom ?: "")
            background.background = createBackground(mCoursesColors[index])
            mAffairBackground.visibility = View.GONE
            if (itemCount > 1) {
                tag.visibility = View.VISIBLE
                tag.background = createTagBackground(mCoursesTextColors[course.hashLesson])
            }
            top.textColor = mCoursesTextColors[index]
            bottom.textColor = mCoursesTextColors[index]
        } else {
            if (mShowModel) {
                top.text = course.course
                bottom.text = course.classroom
            }
            top.textColor = ContextCompat.getColor(mContext, R.color.levelTwoFontColor)
            bottom.textColor = ContextCompat.getColor(mContext, R.color.levelTwoFontColor)
            mAffairBackground.visibility = View.VISIBLE
        }
    }

    /**
     * 这个方法来制造课表item的圆角背景
     * @param rgb 背景颜色
     * 里面的圆角的参数是写在资源文件里的
     */
    private fun createBackground(rgb: Int): Drawable {
        val drawable = GradientDrawable()
        val courseCorner = mContext.resources.getDimension(R.dimen.course_course_item_radius)
        drawable.cornerRadii = floatArrayOf(courseCorner, courseCorner, courseCorner, courseCorner, courseCorner, courseCorner, courseCorner, courseCorner)
        drawable.setColor(rgb)
        return drawable
    }

    /**
     * 这个方法来制造课表item的圆角背景
     * @param rgb 背景颜色
     * 里面的圆角的参数是写在资源文件里的
     */
    private fun createTagBackground(rgb: Int): Drawable {
        val drawable = GradientDrawable()
        val courseCorner = mContext.resources.getDimension(R.dimen.course_schedule_tag_radius)
        drawable.cornerRadii = floatArrayOf(courseCorner, courseCorner, courseCorner, courseCorner, courseCorner, courseCorner, courseCorner, courseCorner)
        drawable.setColor(rgb)
        return drawable
    }


    /**
     * 获取当前课程位置上的高度信息
     * @param
     */
    override fun getItemViewInfo(row: Int, column: Int): ScheduleView.ScheduleItem? {
        val isOverlap: Boolean = if (row == 1 || row == 3 || row == 5) {
            mSchedulesArray[row - 1][column]?.get(0)?.period ?: NOT_LONG_COURSE == 4
        } else {
            false
        }
        val schedules = mSchedulesArray[row][column]
        return if (schedules == null || schedules.size == 0||isOverlap) {
            null
        } else {
            ScheduleView.ScheduleItem(itemHeight = schedules[0].period)
        }
    }

    override fun getHighLightPosition(): Int? {
        val schoolCalendar = SchoolCalendar()
        val now = Calendar.getInstance()
        val nowWeek = schoolCalendar.weekOfTerm
        if (nowWeek == mNowWeek) {
            return (now.get(Calendar.DAY_OF_WEEK) + 5) % 7
        }
        return null
    }


    /**
     * 此方法用于自定义ItemView点击事件
     * @param itemView 显示课程的Item
     * @param schedules 课程信息
     */
    private fun setItemViewOnclickListener(itemView: View, schedules: MutableList<Course>) {
        itemView.setOnClickListener {
            mDialogHelper.showDialog(schedules)
            EventBus.getDefault().post(DismissAddAffairViewEvent())
        }
    }


    object CourseComparator : Comparator<Course> {

        /**
         * 排序的方式是课程排在事务的前面，课程中的排序是时间长的排在时间短的前面。
         * @param behind 现在排在后面的Course
         * @param front 现在排在前面的Course
         */
        override fun compare(behind: Course, front: Course): Int {
            if ((front.period >= behind.period && front.customType < behind.customType) || (front.period > behind.period && front.customType <= behind.customType)) {
                return 1
            }
            return -1
        }
    }
}