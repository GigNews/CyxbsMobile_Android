package com.cyxbs.pages.widget.repo.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mredrock.cyxbs.api.course.ILessonService
import java.io.Serializable

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 */
@Entity
data class LessonEntity(
    val stuNum: String = "",
    val week: Int = 0, // 第几周的课
    val beginLesson: Int = 0, // 开始节数，如：1、2 节课以 1 开始；3、4 节课以 3 开始，注意：中午是以 -1 开始，傍晚是以 -2 开始
    val classroom: String = "", // 教室
    val course: String = "", // 课程名
    val courseNum: String = "", // 课程号
    val day: String = "", // 星期几，这是字符串的星期几：星期一、星期二......
    val hashDay: Int = 0, // 星期数，星期一为 0
    var period: Int = 2, // 课的长度
    val rawWeek: String = "", // 周期
    val teacher: String = "",
    val type: String = "", // 选修 or 必修
    @PrimaryKey
    val id: String = "${stuNum}-${week}-${hashDay}-${beginLesson}",
) : Serializable {
    companion object {
        //将api模块的Lesson转化为widget模块的Lesson
        fun convertFromApi(apiLessons: List<ILessonService.Lesson>): ArrayList<LessonEntity> {
            val lessons = arrayListOf<LessonEntity>()
            for (apiLesson in apiLessons) {
                val lesson = LessonEntity(
                    stuNum = apiLesson.stuNum,
                    week = apiLesson.week,
                    beginLesson = apiLesson.beginLesson,
                    classroom = apiLesson.classroom,
                    course = apiLesson.course,
                    courseNum = apiLesson.courseNum,
                    day = apiLesson.day,
                    hashDay = apiLesson.hashDay,
                    period = apiLesson.period,
                    rawWeek = apiLesson.rawWeek,
                    teacher = apiLesson.teacher,
                    type = apiLesson.type)
                lessons.add(lesson)
            }
            return lessons
        }

        fun LessonEntity.convertToApi(): ILessonService.Lesson {
            return ILessonService.Lesson(
                stuNum = this.stuNum,
                week = this.week,
                beginLesson = this.beginLesson,
                classroom = this.classroom,
                course = this.course,
                courseNum = this.courseNum,
                day = this.day,
                hashDay = this.hashDay,
                period = this.period,
                rawWeek = this.rawWeek,
                teacher = this.teacher,
                type = this.type
            )
        }
    }
}
