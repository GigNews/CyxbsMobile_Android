package com.cyxbs.pages.todo.model.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Author: RayleighZ
 * Time: 2021-08-29 0:51
 */
data class DelPushWrapper(
    @SerializedName("del_todo_array")
    val delTodoList: List<Long>,
    @SerializedName("sync_time")
    val syncTime: Long,
    @SerializedName("force")
    var force: Int = 1
): Serializable
