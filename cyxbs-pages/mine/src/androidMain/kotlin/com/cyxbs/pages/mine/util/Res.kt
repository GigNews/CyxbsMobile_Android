package com.cyxbs.pages.mine.util

import com.cyxbs.components.utils.network.ApiGenerator
import com.cyxbs.pages.mine.network.ApiService

/**
* Created by zia on 2018/8/20.
*/
val apiService: ApiService by lazy { ApiGenerator.getApiService(ApiService::class.java) }
