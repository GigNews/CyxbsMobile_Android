package com.mredrock.cyxbs.main.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.main.bean.BindingResponse
import com.mredrock.cyxbs.main.bean.StartPage
import com.mredrock.cyxbs.mine.network.model.ScoreStatus
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created By jay68 on 2018/8/10.
 */
interface ApiService {
    @GET("/magipoke-text/start/photo")
    fun getStartPage(): Observable<RedrockApiWrapper<List<StartPage>>>

    /**
     * 检查是否绑定信息
     */
    @FormUrlEncoded
    @POST("/user-secret/user/bind/is")
    fun checkBinding(@Field("stu_num") stu_num: String): Observable<RedrockApiWrapper<BindingResponse>>

    /**
     * 检查是否签到
     */
    @POST("/magipoke-intergral/QA/User/getScoreStatus")
    fun getCheckInStatus(): Observable<RedrockApiWrapper<ScoreStatus>>
}