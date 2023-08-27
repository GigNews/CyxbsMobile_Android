package com.mredrock.cyxbs.course.page.find.ui.course.stu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mredrock.cyxbs.api.crash.ICrashService
import com.mredrock.cyxbs.course.BuildConfig
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.data.toStuLessonData
import com.mredrock.cyxbs.course.page.course.model.StuLessonRepository
import com.mredrock.cyxbs.course.page.find.ui.course.base.BaseFindViewModel
import com.mredrock.cyxbs.lib.utils.service.impl
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 16:55
 */
class FindStuCourseViewModel : BaseFindViewModel<StuLessonData>() {
  
  override val findLessonData: LiveData<Map<Int, List<StuLessonData>>>
    get() = _findLessonData
  private val _findLessonData = MutableLiveData<Map<Int, List<StuLessonData>>>()
  
  private var mOldStuNum = ""
  
  /**
   * 尝试刷新数据，如果是之前已经有的数据，则不会进行刷新
   */
  fun tryFreshData(stuNum: String) {
    if (stuNum != mOldStuNum) {
      mOldStuNum = stuNum
      StuLessonRepository.getLesson(stuNum)
        .map { it.toStuLessonData() }
        .map { list ->
          list.groupBy { it.week }
        }.observeOn(AndroidSchedulers.mainThread())
        .doOnError {
          if (BuildConfig.DEBUG) {
            toast("请求课表出现异常")
            ICrashService::class.impl
              .showCrashDialog(it)
          }
        }.safeSubscribeBy {
          _findLessonData.value = it
        }
    }
  }
}