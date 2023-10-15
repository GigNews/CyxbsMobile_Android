package com.mredrock.cyxbs.course.page.course.model

import androidx.annotation.WorkerThread
import androidx.collection.ArrayMap
import androidx.core.content.edit
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.api.course.ILessonService
import com.mredrock.cyxbs.course.page.course.bean.StuLessonBean
import com.mredrock.cyxbs.course.page.course.room.LessonDataBase
import com.mredrock.cyxbs.course.page.course.room.StuLessonEntity
import com.mredrock.cyxbs.course.page.course.network.CourseApiServices
import com.mredrock.cyxbs.lib.utils.network.api
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.config.config.SchoolCalendar
import com.mredrock.cyxbs.config.sp.defaultSp
import com.mredrock.cyxbs.lib.utils.extensions.*
import com.mredrock.cyxbs.lib.utils.utils.judge.NetworkUtil
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.rx3.asObservable
import java.util.concurrent.TimeUnit

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2022/5/3 18:23
 */
@Suppress("LiftReturnOrAssignment")
object StuLessonRepository {
  
  private val mStuDB by lazyUnlock { LessonDataBase.INSTANCE.getStuLessonDao() }

  // 上一次更新的天数差
  private var mLastUpdateDay: Int
    get() {
      val current = System.currentTimeMillis()
      val timestamp = defaultSp.getLong("上一次更新课表的时间戳", current)
      return TimeUnit.DAYS.convert(current - timestamp, TimeUnit.MILLISECONDS).toInt()
    }
    set(value) {
      defaultSp.edit {
        putLong(
          "上一次更新课表的时间戳",
          System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(value.toLong(), TimeUnit.DAYS)
        )
      }
    }
  
  /**
   * 观察当前登录人的课
   * - 支持换账号登录后返回新登录人的数据
   * - 第一次观察时会请求新的数据
   * - 使用了 distinctUntilChanged()，只会在数据更改了才会回调
   * - 没登录时发送 emptyList()
   * - 没有连接网络并且不允许使用本地缓存时会一直不发送数据给下游
   * - 不会抛出异常给下游
   *
   * @param isForce 是否强制获取数据，如果不强制的话，将使用临时缓存
   * @param isToast 是否提示课表正在使用缓存数据
   */
  fun observeSelfLesson(
    isForce: Boolean = false,
    isToast: Boolean = false,
  ): Observable<List<StuLessonEntity>> {
    return IAccountService::class.impl
      .getUserService()
      .observeStuNumState()
      .observeOn(Schedulers.io())
      .switchMap { value ->
        // 使用 switchMap 可以停止之前学号的订阅
        value.nullUnless(Observable.just(emptyList())) { stuNum ->
          if (ILessonService.isUseLocalSaveLesson) {
            LessonDataBase.INSTANCE.getStuLessonDao()
              .observeLesson(stuNum)
              .doOnSubscribe {
                // 在开始订阅时异步请求一次云端数据，所以下游会先拿到本地数据库中的数据，如果远端数据更新了，整个流会再次通知
                refreshLesson(stuNum, isForce).doOnError {
                  if (isToast) {
                    val lastUpdateDay = mLastUpdateDay
                    if (lastUpdateDay < 1) {
                      toastLong("课表正在使用缓存\n不能保证数据正确性")
                    } else {
                      toastLong("已 $lastUpdateDay 天未更新课表\n建议联网更新")
                    }
                  }
                }.unsafeSubscribeBy {
                  mLastUpdateDay = 0
                }
              }.distinctUntilChanged() // 去重
              .subscribeOn(Schedulers.io())
          } else {
            // 不允许使用本地数据时
            flow {
              // 直到第一次网络请求成功前一直挂起，不返回结果给下游
              NetworkUtil.suspendUntilAvailable()
              emit(Unit)
            }.asObservable()
              .switchMap {
                refreshLesson(stuNum, isForce)
                  .onErrorComplete() // 网络请求的异常全部吞掉
                  .toObservable()
              }
          }
        }
      }
  }
  
  /**
   * 掌邮应用生命周期内的缓存
   *
   * 因为 jwzx 不允许我们本地保存课表数据，但关联人那里有显示和不显示的逻辑，会重复获取课表数据，
   * 但存在用户打开网络后关闭网络的情况，所以需要单独做一层缓存
   */
  private val mCacheForProcessLifecycle = ArrayMap<String, List<StuLessonEntity>>()
  
  /**
   * 刷新某人的课，会抛出网络异常
   * @param isForce 是否强制刷新，如果不采取强制刷新，则会优先从 [mCacheForProcessLifecycle] 查找数据
   */
  fun refreshLesson(stuNum: String, isForce: Boolean = false): Single<List<StuLessonEntity>> {
    if (stuNum.isBlank()) return Single.error(IllegalArgumentException("学号不能为空！"))
    if (!isForce) {
      val cache = mCacheForProcessLifecycle[stuNum]
      if (cache != null) return Single.just(cache)
    }
    return CourseApiServices::class.api
      .getStuLesson(stuNum)
      .map { bean ->
        if (bean.status == 30001) {
          toast("当前教务在线不可用")
        }
        // 检查网络请求数据是否出错
        bean.throwApiExceptionIfFail()
        httpFromStuSuccess(bean)
      }.doOnSuccess {
        mCacheForProcessLifecycle[stuNum] = it
      }.subscribeOn(Schedulers.io())
  }
  
  /**
   * 得到某人的课，在得不到这个人课表数据时会抛出异常，
   */
  fun getLesson(
    stuNum: String,
  ): Single<List<StuLessonEntity>> {
    return refreshLesson(stuNum)
      .interceptException {
        if (ILessonService.isUseLocalSaveLesson) {
          val list = mStuDB.getLesson(stuNum)
          if (list.isNotEmpty()) {
            // 这里允许使用本地缓存的课表数据
            emitter.onSuccess(list)
          } else {
            // 本地数据库没有课时抛出异常
            // 当然，如果这个人是大四的本来就没有课也会抛，但是这里是网络请求出错时的处理，一般情况下不会遇到该情况
            // 除非刚好他没得课，并且网络请求也出错
            throw throwable
          }
        } else {
          throw throwable
        }
      }
  }
  
  @WorkerThread
  private fun httpFromStuSuccess(bean: StuLessonBean): List<StuLessonEntity> {
    if (bean.judgeVersion(true)) {
      val list = bean.toStuLessonEntity()
      // 更新日历
      SchoolCalendar.updateFirstCalendar(bean.nowWeek)
      mStuDB.resetData(bean.stuNum, list)
      return list
    } else {
      if (!ILessonService.isUseLocalSaveLesson) {
        // 不允许本地保存课表时直接返回数据
        return bean.toStuLessonEntity()
      }
      val localList = mStuDB.getLesson(bean.stuNum)
      if (localList.isNotEmpty()) {
        // 使用本地数据
        return localList
      } else {
        // 本地没有数据，只能使用网络数据
        val newList = bean.toStuLessonEntity()
        mStuDB.resetData(bean.stuNum, newList)
        return newList
      }
    }
  }
}