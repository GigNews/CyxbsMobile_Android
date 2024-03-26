package com.mredrock.cyxbs.course.page.course.ui.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.api.affair.IAffairService
import com.mredrock.cyxbs.api.course.ICourseService
import com.mredrock.cyxbs.api.course.ILessonService
import com.mredrock.cyxbs.api.widget.IWidgetService
import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.data.toAffairData
import com.mredrock.cyxbs.course.page.course.data.toStuLessonData
import com.mredrock.cyxbs.course.page.course.model.StuLessonRepository
import com.mredrock.cyxbs.course.page.course.room.LessonDataBase
import com.mredrock.cyxbs.course.page.link.model.LinkRepository
import com.mredrock.cyxbs.course.page.link.room.LinkStuEntity
import com.mredrock.cyxbs.course.service.CourseServiceImpl
import com.mredrock.cyxbs.course.service.toLesson
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.service.impl
import com.mredrock.cyxbs.lib.utils.utils.judge.NetworkUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.asObservable

/**
 * 注意：整个课表采用了观察者模式。数据库对应的数据改变，会自动修改视图内容，这是一种声明式的设计
 *
 * ## 如何更新课表数据 ？
 * 如果你在其他模块添加了事务，然后你想更新课表，那么你应该去更新事务数据库，然后课表会自动收到更新的数据，显示对应的视图。
 *
 * ## 如果后面课表不显示怎么排查 ？
 * 课表数据由自己课程、关联人课程、自己事物三个数据流进行合并，
 * 优先在 [initObserve] 中对每个数据流打上日志，先判断是哪个数据流的问题，再依次深入排查
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/27 17:12
 */
class HomeCourseViewModel : BaseViewModel() {

  companion object {
    private const val TAG = "HomeCourseViewModel"
  }
  
  private val _homeWeekData = MutableLiveData<Map<Int, HomePageResult>>()
  val homeWeekData: LiveData<Map<Int, HomePageResult>> get() = _homeWeekData
  
  private val _linkStu = MutableLiveData<LinkStuEntity>()
  val linkStu: LiveData<LinkStuEntity> get() = _linkStu
  
  private val _showLinkEvent = MutableSharedFlow<Boolean>()
  val showLinkEvent: SharedFlow<Boolean> get() = _showLinkEvent
  
  val courseService = ICourseService::class.impl as CourseServiceImpl
  
  // Vp2 的 currentItem
  val currentItem = MutableLiveData<Int>()
  
  /**
   * 改变关联人的可见性
   */
  fun changeLinkStuVisible(isShowLink: Boolean) {
    LinkRepository.changeLinkStuVisible(isShowLink)
      .safeSubscribeBy {
        viewModelScope.launch {
          _showLinkEvent.emit(isShowLink)
        }
      }
    // 这里更新后，所有观察关联人的地方都会重新发送新数据
  }
  
  private var mDataObserveDisposable = initObserve(true)
  
  /**
   * 取消课表数据的观察流
   *
   * 建议与 [refreshDataObserve] 配合使用
   */
  fun cancelDataObserve() {
    if (!mDataObserveDisposable.isDisposed) {
      mDataObserveDisposable.dispose()
    }
  }
  
  /**
   * 刷新整个课表数据的观察流，相当于刷新课表数据
   */
  fun refreshDataObserve() {
    cancelDataObserve()
    LessonDataBase.lessonVerDao.clear() // 清空课程数据版本号，强制使用网络数据
    mDataObserveDisposable = initObserve(false)
  }
  
  /**
   * 注意：整个课表采用了观察者模式。数据库对应的数据改变，会自动修改视图内容
   */
  private fun initObserve(isToast: Boolean): Disposable {
    // 自己课的观察流
    val selfLessonObservable = StuLessonRepository
      .observeSelfLesson(isToast = isToast)
      .doOnNext {
        Log.d(TAG, "selfLesson: $it")
      }

    // 关联人课的观察流
    val linkLessonObservable = LinkRepository.observeLinkStudent()
      .doOnNext { _linkStu.postValue(it) }
      .switchMap { entity ->
        // 没得关联人和不显示关联课程时发送空数据
        if (entity.isNull() || !entity.isShowLink) Observable.just(emptyList()) else {
          flow {
            if (!ILessonService.isUseLocalSaveLesson) {
              // 如果不允许使用本地数据就挂起直到网络连接成功
              NetworkUtil.suspendUntilAvailable()
            }
            emit(Unit)
          }.asObservable()
            .flatMap {
              // 在没有连接网络时 StuLessonRepository.getLesson() 方法会抛出异常
              StuLessonRepository.getLesson(entity.linkNum).toObservable()
            }.onErrorReturn {
              emptyList()
            }
        }.doOnNext {
          Log.d(TAG, "linkLesson: $it")
        }
      }
  
    // 事务的观察流
    val affairObservable = IAffairService::class.impl
      .observeSelfAffair()
      .doOnNext {
        Log.d(TAG, "affair: $it")
      }

    // 合并观察流
    return Observable.combineLatest(
      selfLessonObservable,
      linkLessonObservable,
      affairObservable
    ) { self, link, affair ->
      try {
        // 刷新小组件
        IWidgetService::class.impl
          .notifyWidgetRefresh(self.toLesson(), link.toLesson(), affair)
      } catch (e: Exception) {
        Log.d(TAG, "刷新小组件发生异常：\n${e.stackTraceToString()}")
      }
      // 装换为 data 数据类
      HomePageResultImpl.flatMap(
        self.toStuLessonData(),
        link.toStuLessonData(),
        affair.toAffairData()
      )
    }.doOnError {
      Log.d(TAG, "合并课表数据流发生异常：\n${it.stackTraceToString()}")
    }.subscribeOn(Schedulers.io())
      .safeSubscribeBy {
        _homeWeekData.postValue(it)
      }
  }
  
  
  
  
  interface HomePageResult {
    val self: List<StuLessonData>
    val link: List<StuLessonData>
    val affair: List<AffairData>
    
    companion object Empty : HomePageResult {
      override val self: List<StuLessonData>
        get() = emptyList()
      override val link: List<StuLessonData>
        get() = emptyList()
      override val affair: List<AffairData>
        get() = emptyList()
    }
  }
  
  data class HomePageResultImpl(
    override val self: MutableList<StuLessonData> = arrayListOf(),
    override val link: MutableList<StuLessonData> = arrayListOf(),
    override val affair: MutableList<AffairData> = arrayListOf()
  ) : HomePageResult {
    companion object {
      fun flatMap(
        self: List<StuLessonData>,
        link: List<StuLessonData>,
        affair: List<AffairData>
      ) : Map<Int, HomePageResultImpl> {
        return buildMap {
          self.forEach {
            getOrPut(it.week) { HomePageResultImpl() }
              .self.add(it)
          }
          link.forEach {
            getOrPut(it.week) { HomePageResultImpl() }
              .link.add(it)
          }
          affair.forEach { data ->
            if (data.week == 0) {
              // 因为 affair 模块那边对于整学期的事务使用 week = 0 来记录，
              // 所以这里需要单独做适配，把 week = 0 扩展到每一周去
              repeat(ICourseService.maxWeek) {
                getOrPut(it + 1) { HomePageResultImpl() }
                  .affair.add(data.copy(week = it + 1))
              }
            } else {
              getOrPut(data.week) { HomePageResultImpl() }
                .affair.add(data)
            }
          }
        }
      }
    }
  }
}