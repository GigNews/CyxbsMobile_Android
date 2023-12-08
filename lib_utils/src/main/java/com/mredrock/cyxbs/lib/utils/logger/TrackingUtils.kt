package com.mredrock.cyxbs.lib.utils.logger

import com.mredrock.cyxbs.lib.utils.BuildConfig
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.logger.bean.TrackingResultBean
import com.mredrock.cyxbs.lib.utils.logger.event.ClickEvent
import com.mredrock.cyxbs.lib.utils.logger.network.TrackingApiService
import com.mredrock.cyxbs.lib.utils.utils.LogUtils

/**
 * @author : why
 * @time   : 2023/12/5 19:08
 * @bless  : God bless my code
 */

/**
 * 用于埋点上报的工具类
 */
object TrackingUtils {

  private const val TAG = "LoggerUtils"

  /**
   * 点击事件上报
   * @return [TrackingResultBean] -> 即返回的对应状态
   *
   * null -> 异常状态，说明返回的状态未知
   */
  suspend fun trackClickEvent(clickEvent: ClickEvent): Result<TrackingResultBean?> {
    return trackEvent(clickEvent.mapParams)
  }

  /**
   * 点击事件上报
   * @return [TrackingResultBean] -> 即返回的对应状态
   *
   * null -> 异常状态，说明返回的状态未知
   */
  suspend fun trackEvent(params: Map<String, String>): Result<TrackingResultBean?> {
    return runCatching {
      TrackingApiService.INSTANCE.trackEvent(params)
        .data.status.let { status ->
          LogUtils.d(TAG, "(LoggerUtils.kt:46)-->> trackingEvent, status = $status")
          TrackingResultBean.values().find { status == it.status }.also {
            if (it != TrackingResultBean.SUCCESS) {
              // 网络请求成功但参数异常
              toastLoggerWhenDebug(it)
            }
            return Result.success(it)
          }
        }
    }.onFailure {
      // 异常
      // 需求千万条，稳定第一条。错误catch住，debug模式下 toast弹窗 + 堆栈输出
      it.printStackTrace()
      toastLoggerWhenDebug(null)
      return Result.failure(it)
    }
  }

  fun toastLogger(trackingResultBean: TrackingResultBean?) {
    val msg = trackingResultBean?.msg ?: "埋点上报失败，请查看网络请求日志！"
    msg.toast()
    LogUtils.d(TAG, "(LoggerUtils.kt:67)-->> toast text:$msg")
  }

  /**
   * 1. `ID wrong` 没有与请求的id参数相同的埋点
   * 2. `hash wrong` 请求对应的埋点的hash值不匹配
   * 3. `null` 埋点网络请求异常
   */
  fun toastLoggerWhenDebug(trackingResultBean: TrackingResultBean? = null) {
    if (BuildConfig.DEBUG) {
      toastLogger(trackingResultBean)
    }
  }
}