package com.cyxbs.pages.noclass.bean

import com.mredrock.cyxbs.api.course.ICourseService
import com.mredrock.cyxbs.api.course.ILessonService
import java.io.Serializable

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.cyxbs.pages.noclass.bean
 * @ClassName:      NoClassSpareTime
 * @Author:         Yan
 * @CreateDate:     2022年09月06日 23:43:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    用于记录所有人的没课情况
 */

data class NoClassSpareTime (   //todo 一周的空闲时间
    //记录一天的空闲时间
    val spareDayTime : HashMap<Int,SpareLineTime>
) : Serializable {
  
  companion object{
    val EMPTY_PAGE = HashMap<Int, NoClassSpareTime>().apply {
      (0 .. ICourseService.maxWeek).forEach{
        this[it] = getNewEmptySpareTime()
      }
    }
  }
  
  //一个学生的map 用于学号与姓名之间的转换
  var mIdToNameMap : HashMap<String,String> = hashMapOf()
  
  //记录从上当下一行的空闲时间
  data class SpareLineTime(  //todo 一天的空闲时间
    //此时有空闲时间的学号
    val SpareItem: ArrayList<SpareIds>,   //todo 对应一天格子的集合
  ): Serializable {
    //每格格子上空闲的人数
    data class SpareIds(
      val spareId : ArrayList<String>  //todo 每个格子的空闲的人数
    ) : Serializable
  }
}

  /**
   * 将返回出来的数据变为SpareTime格式
   *
   * @receiver key: 学号; value: 所有课程
   * @return key: 周数; value: 当前周的空闲时间
   */
fun Map<String, List<ILessonService.Lesson>>.toSpareTime() : HashMap<Int, NoClassSpareTime>{
  val studentSpareTimes: HashMap<Int, NoClassSpareTime> = HashMap()
  //所有学生的id
  val stuIds = keys.toList()
  studentSpareTimes[0] = getNewSpareTime(stuIds)
  //整个学期的没课约
  val semesterStu = studentSpareTimes[0]!!
  //学生为划分
  this.forEach { entry ->
    //每一节课为划分
    entry.value.forEach { lesson ->
      //得到当前lesson页面的SpareTime
      if (studentSpareTimes[lesson.week] == null) {
        studentSpareTimes[lesson.week] = getNewSpareTime(stuIds)
      }
      //当前页面的
      val stu: NoClassSpareTime = studentSpareTimes[lesson.week]!!
      //当前页面第几竖列
      val line = stu.spareDayTime[lesson.hashDay]
      //整个学期页面的第几行
      val semesterLine = semesterStu.spareDayTime[lesson.hashDay]
      (0 until lesson.period).map {
        //得到了当前一整竖行的数据
        //检查移除该格子的空闲人
        if (line!!.SpareItem[lesson.beginLesson + it].spareId.contains(lesson.stuNum)) {
          line.SpareItem[lesson.beginLesson + it].spareId.remove(lesson.stuNum)
          semesterLine!!.SpareItem[lesson.beginLesson + it].spareId.remove(lesson.stuNum)
        }
      }
    }
  }
    //检查全部包含进去
    if (studentSpareTimes.size <= ICourseService.maxWeek){
      (0 .. ICourseService.maxWeek).map{
        if (studentSpareTimes[it] == null){
          studentSpareTimes[it] = getNewSpareTime(stuIds)
        }
      }
    }

    return studentSpareTimes
}

/**
 * 一个新的SpareTime对象
 */
private fun getNewSpareTime(stuIds : List<String>): NoClassSpareTime {  //todo 创建的是一个实际的空闲时间对象
  return NoClassSpareTime(hashMapOf()).apply {
    // 0 .. 6 指星期数
    (0..6).map {
      spareDayTime[it] =
        NoClassSpareTime.SpareLineTime(
          ArrayList<NoClassSpareTime.SpareLineTime.SpareIds>(13).apply {
            // 13 是总的行数，为上午 4 行，中午 1 行，下午 4 行，傍晚 1 行，晚上 4 行
            (0..13).forEach { _ ->
              add(NoClassSpareTime.SpareLineTime.SpareIds((ArrayList(stuIds))))
            }
          }
        )
    }
  }
}


private fun getNewEmptySpareTime(): NoClassSpareTime {  //todo 创建一个空的空闲时间对象
  return NoClassSpareTime(hashMapOf()).apply {
    (0..6).map {
      spareDayTime[it] =
        NoClassSpareTime.SpareLineTime(
          ArrayList<NoClassSpareTime.SpareLineTime.SpareIds>(13).apply {
            (0..13).forEach { _ ->
              add(NoClassSpareTime.SpareLineTime.SpareIds(arrayListOf()))
            }
          }
        )
    }
  }
}

