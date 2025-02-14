package com.cyxbs.pages.mine.page.feedback.utils

import android.content.Context
import android.content.SharedPreferences

private const val SHARED_PREFERENCE_NAME = "POINT_READ_STATE_SP"

object SPUtils {

}

fun Context.getPointStateSharedPreference(): SharedPreferences? {
    return getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
}