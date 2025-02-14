package com.cyxbs.pages.ufield.network

import com.cyxbs.components.utils.network.ApiGenerator
import com.cyxbs.components.utils.network.ApiWrapper
import com.cyxbs.pages.ufield.bean.IsAdminBean
import com.cyxbs.pages.ufield.bean.ItemActivityBean
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

/**
 *  description :
 *  author : lytMoon
 *  date : 2023/8/20 19:02
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */
interface UFieldApiService {
    companion object {
        val INSTANCE by lazy { ApiGenerator.getApiService(UFieldApiService::class) }
    }

    /**
     * 获得全部都数据
     */
    @GET("/magipoke-ufield/activity/list/all")
    fun getAllActivityData(): Single<ApiWrapper<ItemActivityBean>>


    /**
     * 获取文娱活动数据
     */
    @GET("/magipoke-ufield/activity/list/all?activity_type=culture")
    fun getCultureList(): Single<ApiWrapper<ItemActivityBean>>

    /**
     * 获取体育活动数据
     */
    @GET("/magipoke-ufield/activity/list/all?activity_type=sports")
    fun getSportsList(): Single<ApiWrapper<ItemActivityBean>>

    /**
     * 获取教育活动数据
     */
    @GET("/magipoke-ufield/activity/list/all?activity_type=education")
    fun getEducationList(): Single<ApiWrapper<ItemActivityBean>>

    /**
     * 查看是否具有管理员权限
     */

    @GET("/magipoke-ufield/isadmin/")
    fun getIsAdmin():Single<ApiWrapper<IsAdminBean>>



}