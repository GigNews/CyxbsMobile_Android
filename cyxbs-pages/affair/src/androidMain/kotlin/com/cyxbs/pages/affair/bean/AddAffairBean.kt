package com.cyxbs.pages.affair.bean

import com.google.gson.annotations.SerializedName
import com.cyxbs.components.utils.network.IApiStatus
import java.io.Serializable

data class AddAffairBean(
  @SerializedName("id")
  val remoteId: Int,
  @SerializedName("info")
  override val info: String,
  @SerializedName("state")
  val state: Int,
  @SerializedName("status")
  override val status: Int
) : Serializable, IApiStatus