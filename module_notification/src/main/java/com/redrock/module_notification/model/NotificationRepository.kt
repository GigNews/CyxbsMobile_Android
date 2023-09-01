package com.redrock.module_notification.model

import com.google.gson.Gson
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.redrock.module_notification.bean.AffairDateBean
import com.redrock.module_notification.bean.ChangeItineraryReadStatusUploadBean
import com.redrock.module_notification.bean.MsgBeanData
import com.redrock.module_notification.bean.ReceivedItineraryMsgBean
import com.redrock.module_notification.bean.SentItineraryMsgBean
import com.redrock.module_notification.bean.toAffairDateBean
import com.redrock.module_notification.network.ApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/20
 * @Description:
 *
 */
object NotificationRepository {
    private val api = ApiService.INSTANCE
    private val mGson = Gson()


    private fun ReceivedItineraryMsgBean.ItineraryDateBean.toPostDateJson(): String {
        return mGson.toJson(toAffairDateBean())
    }
    /**
     * 获取所有的活动消息和系统消息
     */
    fun getAllSysAndActMsg(): Observable<RedrockApiWrapper<MsgBeanData>> {
        return api.getAllMsg()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取本用户已经发送的行程
     */
    fun getSentItinerary() : Single<ApiWrapper<List<SentItineraryMsgBean>>> {
        return api.getSentItinerary()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 获取通知到本用户的行程
     */
    fun getReceivedItinerary() : Single<ApiWrapper<List<ReceivedItineraryMsgBean>>> {
        return api.getReceivedItinerary()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 取消行程的提醒
     * @param itineraryId 行程id
     */
    fun cancelItineraryReminder(itineraryId: Int) : Single<ApiStatus> {
        return api.cancelItineraryReminder(itineraryId.toString())
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 改变行程的已读状态（默认为将行程变为已读，即status为true时）
     * @param idList 行程id
     */
    fun changeItineraryReadStatus(idList: List<Int>, status: Boolean = true) : Single<ApiStatus> {
        return api.changeItineraryReadStatus(ChangeItineraryReadStatusUploadBean(idList, status))
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 改变行程的是否被添加到日程状态（默认为将行程变为已添加，即status为true时）
     * @param itineraryId 行程id
     */
    fun changeItineraryAddStatus(itineraryId: Int, status: Boolean = true) : Single<ApiStatus> {
        return api.changeItineraryAddStatus(itineraryId, status)
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 添加行程到事务中
     *
     * @param time
     * @param title
     * @param content
     * @param dateJson
     * @return
     */
    fun addAffair(remindTime: Int, info: ReceivedItineraryMsgBean) : Single<ApiStatus>{
        return api.addAffair(remindTime, info.title, info.content, info.dateJson.toPostDateJson())
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }
}