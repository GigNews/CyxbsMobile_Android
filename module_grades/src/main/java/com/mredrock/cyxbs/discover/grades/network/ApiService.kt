package com.mredrock.cyxbs.discover.grades.network

import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.discover.grades.bean.Exam
import com.mredrock.cyxbs.discover.grades.bean.IdsBean
import com.mredrock.cyxbs.discover.grades.bean.IdsStatus
import com.mredrock.cyxbs.discover.grades.bean.analyze.GPAStatus
import io.reactivex.Observable
import retrofit2.http.*

/**
 * @CreateBy: FxyMine4ever
 *
 * @CreateAt:2018/9/16
 */
interface ApiService {


    /**
     * 获取考试信息（不含补考）
     */
    @FormUrlEncoded
    @POST("/api/examSchedule")
    fun getExam(@Field("stuNum") stuNum: String): Observable<RedrockApiWrapper<List<Exam>>>

    /**
     * 获取补考信息
     */
    @FormUrlEncoded
    @POST("/api/examReexam")
    fun getReExam(@Field("stuNum") stu: String): Observable<RedrockApiWrapper<List<Exam>>>

    @POST("wxapi/magipoke/ids/bind")
    fun bindIds(@Body idsBean: IdsBean) : Observable<IdsStatus>

    @GET("wxapi/magipoke/gpa")
    fun getAnalyzeData(): Observable<GPAStatus>

}