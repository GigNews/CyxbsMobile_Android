package com.cyxbs.pages.notification.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cyxbs.components.utils.extensions.setSchedulers
import com.cyxbs.components.utils.network.ApiGenerator
import com.cyxbs.components.utils.network.mapOrInterceptException
import com.cyxbs.components.utils.network.mapOrThrowApiException
import com.cyxbs.components.utils.utils.LogUtils
import com.cyxbs.pages.notification.bean.ChangeReadStatusToBean
import com.cyxbs.pages.notification.bean.DeleteMsgToBean
import com.cyxbs.pages.notification.bean.ItineraryAllMsg
import com.cyxbs.pages.notification.bean.SystemMsgBean
import com.cyxbs.pages.notification.bean.UfieldMsgBean
import com.cyxbs.pages.notification.network.ApiService
import com.cyxbs.pages.notification.util.Constant.NOTIFICATION_LOG_TAG
import com.cyxbs.components.base.ui.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Author by OkAndGreat
 * Date on 2022/4/27 17:08.
 *
 */
class NotificationViewModel : BaseViewModel() {

    private val _ufieldActivityMsg = MutableLiveData<List<UfieldMsgBean>>()

    val ufieldActivityMsg: LiveData<List<UfieldMsgBean>> get() = _ufieldActivityMsg

    // val activeMsg = MutableLiveData<List<ActiveMsgBean>>()
    val systemMsg = MutableLiveData<List<SystemMsgBean>>()

    // 所有可收到的行程消息的列表
    val itineraryMsg: LiveData<ItineraryAllMsg> get() = _itineraryMsg
    private val _itineraryMsg = MutableLiveData<ItineraryAllMsg>()

    // 是否获取成功获取过行程数据
    var getItineraryIsSuccessful: Boolean  = false
        private set

    val checkInStatus = MutableLiveData<Boolean>()

    //通知tablayout行程通知小红点显示状态
    val itineraryDotStatus: LiveData<Boolean> get() = _itineraryDotStatus
    private val _itineraryDotStatus = MutableLiveData<Boolean>()

    //通知tablayout系统通知小红点显示状态
    val sysDotStatus = MutableLiveData<Boolean>()

    //通知tablayout活动通知小红点显示状态
    val activeDotStatus = MutableLiveData<Boolean>()

    //通知popupwindow是否可以点击 当处于多选删除时不可以出现popupwindow
    val popupWindowClickableStatus = MutableLiveData<Boolean>()

    //改变消息已读状态的Status
    val changeMsgReadStatus = MutableLiveData<Int>()

    //获取数据是否成功
    val getMsgSuccessful = MutableLiveData<Boolean>()

    //获取游乐场的消息是否成功
    val getUfieldMsgSuccessful = MutableLiveData<Boolean>()

    private val retrofit by lazy { ApiGenerator.getApiService(ApiService::class.java) }

    /*
    * 获取活动消息
    * */
    fun getUFieldActivity() {
        retrofit.getUFieldActivity()
            .setSchedulers()
            .mapOrThrowApiException()
            .safeSubscribeBy(
                onError = {
                    toast("服务君似乎打盹了呢~")
                    getUfieldMsgSuccessful.value = false
                },
                onNext = {
                    _ufieldActivityMsg.postValue(it)
                    getUfieldMsgSuccessful.value = true
                }
            )

    }
    /*
    * 改变活动消息的读取状态
    * */

    fun changeUfieldMsgStatus(messageId: Int) {
        retrofit.changeUfieldMsgStatus(messageId)
            .setSchedulers()
            .mapOrThrowApiException()
            .safeSubscribeBy()
    }

    /**
     * 获取所有活动和系统通知信息
     */
    fun getAllMsg() {
        retrofit.getAllMsg()
            .setSchedulers()
            .mapOrThrowApiException()
            .safeSubscribeBy(
                onError = {
                    /**
                     * onNext第一次发送null的时候会到onError，第二次就不会了
                     */
                    if (it is NullPointerException) {
                        getAllMsg()
                    } else {
                        Log.w(NOTIFICATION_LOG_TAG, "getAllMsg failed $it")
                        getMsgSuccessful.value = false
                    }
                },
                onNext = {
                    Log.d(NOTIFICATION_LOG_TAG, "getAllMsg: $it")
                    //  activeMsg.value = it.active_msg
                    systemMsg.value = it.system_msg
                    getMsgSuccessful.value = true
                }
            )
    }


    /**
     * 删除消息
     */
    fun deleteMsg(bean: DeleteMsgToBean) {
        retrofit.deleteMsg(bean)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy {
                toast("删除消息成功")
            }
    }

    /**
     * 改变消息已读状态
     * 我们约定position >= 0 为系统通知的消息 <=0 为活动通知的消息
     * 如果是null则是改变所有消息的可读状态
     */
    fun changeMsgStatus(bean: ChangeReadStatusToBean, position: Int? = null) {
        retrofit.changeMsgStatus(bean)
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy {}
    }

    /**
     * 获取签到状态信息
     */
    fun getCheckInStatus() {
        ApiGenerator.getCommonApiService(ApiService::class.java)
            .getCheckInStatus()
            .mapOrThrowApiException()
            .setSchedulers()
            .safeSubscribeBy {
                checkInStatus.value = it.isChecked
            }
    }

    /**
     * 通知改变tablayout系统通知小红点显示状态
     */
    fun changeSysDotStatus(status: Boolean) {
        sysDotStatus.value = status
    }

    /**
     * 通知改变tablayout活动通知小红点显示状态
     */
    fun changeActiveDotStatus(status: Boolean) {
        activeDotStatus.value = status
    }

    /**
     * 通知改变tablayout行程通知小红点显示状态
     */
    fun changeItineraryDotStatus(status: Boolean) {
        _itineraryDotStatus.value = status
    }

    /**
     * 通知改变popupwindow是否可以弹出
     */
    fun changePopUpWindowClickableStatus(status: Boolean) {
        popupWindowClickableStatus.value = status
    }

    /**
     * 获取所有行程消息
     */
    fun getAllItineraryMsg() {
        val tempList = ItineraryAllMsg(emptyList(), emptyList())
        retrofit.getReceivedItinerary()
            .mapOrInterceptException {
                "获取行程消息失败".toast()
                getMsgSuccessful.postValue(false)
            }
            .flatMap {
                tempList.receivedItineraryList = it
                retrofit.getSentItinerary()
            }
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                "获取行程消息失败".toast()
                getMsgSuccessful.postValue(false)
                ApiException {
                // 处理全部 ApiException 错误
                }.catchOther {
                // 处理非 ApiException 的其他异常
                }
            }
            .safeSubscribeBy(
                onError = {
                    "获取行程消息失败".toast()
                    LogUtils.d("H57-err","getAllItineraryMsg fair")
                    getMsgSuccessful.postValue(false)
                },
                onSuccess = {
                    tempList.sentItineraryList = it
                    _itineraryMsg.postValue(tempList)
                    getItineraryIsSuccessful = true
                    getMsgSuccessful.postValue(true)
                }
            )
    }
}