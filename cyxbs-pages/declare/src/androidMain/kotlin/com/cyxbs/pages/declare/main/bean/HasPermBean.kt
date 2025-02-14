package com.cyxbs.pages.declare.main.bean

import com.google.gson.annotations.SerializedName

/**
 * ... 自己是否有发布投票权限数据类
 * @author RQ527 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2023/3/16
 * @Description:
 */
data class HasPermBean(
    @SerializedName("isPerm")
    val isPerm:Boolean)