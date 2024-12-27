package com.cyxbs.pages.volunteer.network

import com.cyxbs.pages.volunteer.bean.VolunteerAffair
import com.cyxbs.pages.volunteer.bean.VolunteerBase
import com.cyxbs.pages.volunteer.bean.VolunteerJudge
import com.cyxbs.pages.volunteer.bean.VolunteerTime
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("/volunteer-message/binding")
    fun volunteerLogin(@Field("account") account: String,
                       @Field("password") password: String): Observable<VolunteerBase>

    @POST("/volunteer-message/select")
    fun getVolunteerRecord(): Observable<VolunteerTime>

    @GET("/cyb-volunteer/volunteer/activity/info/new")
    fun getVolunteerAffair(): Observable<RedrockApiWrapper<List<VolunteerAffair>>>

    @POST("/volunteer-message/judge")
    fun judgeBind(): Observable<VolunteerJudge>

    @POST("/volunteer-message/unbinding")
    fun unbindVolunteerAccount(): Observable<VolunteerBase>
}