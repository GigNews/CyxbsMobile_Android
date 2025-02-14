package com.cyxbs.pages.news.network

import com.cyxbs.pages.news.bean.NewsDetails
import com.cyxbs.pages.news.bean.NewsListItem
import com.cyxbs.components.utils.network.ApiWrapper
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming

/**
 * Author: Hosigus
 * Date: 2018/9/23 12:30
 * Description: com.mredrock.cyxbs.discover.news.network
 */
interface ApiService {
    @GET("/magipoke-jwzx/jwNews/list")
    fun getNewsList(@Query ("page") page: Int): Observable<ApiWrapper<List<NewsListItem>>>

    @GET("/magipoke-jwzx/jwNews/content")
    fun getNewsDetails(@Query("id") id: String,
                       @Query("forceFetch") fetch: Boolean = true): Observable<ApiWrapper<NewsDetails>>

    @Streaming
    @GET("/magipoke-jwzx/jwNews/file")
    fun download(@Query("id") id: String): Call<ResponseBody>
}