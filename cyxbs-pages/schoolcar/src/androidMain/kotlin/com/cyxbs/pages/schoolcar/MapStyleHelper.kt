package com.cyxbs.pages.schoolcar

import android.content.Context
import androidx.core.content.edit
import com.cyxbs.pages.schoolcar.network.MapService
import com.cyxbs.components.config.sp.defaultSp
import com.mredrock.cyxbs.lib.utils.extensions.setSchedulers
import com.mredrock.cyxbs.lib.utils.extensions.unsafeSubscribeBy
import com.mredrock.cyxbs.lib.utils.network.ApiGenerator
import com.mredrock.cyxbs.lib.utils.network.getBaseUrl
import okhttp3.ResponseBody
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MapStyleHelper(val context: Context) {
    private val mapDirRoot = File(context.filesDir, "/maoXhMap")
    private lateinit var style: File
    private lateinit var styleExtra: File
    private var oneFileReady = false


    fun saveMapStyle(callback: () -> Unit) {
        checkFile()
        val retrofit = ApiGenerator.createSelfRetrofit(retrofitConfig = { builder: Retrofit.Builder ->
            builder.baseUrl(getBaseUrl())
        }, tokenNeeded = true)
        val apiService = retrofit.create(MapService::class.java)
        apiService.getMapRes("map_A")
                .setSchedulers()
                .unsafeSubscribeBy(onError = {

                }) {
                    saveToDisk(it, style)
                    fileReady(callback)
                }
        apiService.getMapRes("map_B")
                .setSchedulers()
                .unsafeSubscribeBy {
                    saveToDisk(it, styleExtra)
                    fileReady(callback)
                }
    }

    private fun saveToDisk(responseBody: ResponseBody, file: File) {
        try {
            val byte = responseBody.bytes()
            val os = FileOutputStream(file)
            os.write(byte)
        } catch (exception: IOException) {
        }

    }

    private fun checkFile() {
        if (!mapDirRoot.exists()) mapDirRoot.mkdir()

        styleExtra = File(mapDirRoot, "style_extra.data")
        style = File(mapDirRoot, "style.data")
        if (!styleExtra.exists()) styleExtra.createNewFile()
        if (!style.exists()) style.createNewFile()
    }

    private fun fileReady(callback: () -> Unit) {
        if (oneFileReady) {
            defaultSp.edit {
                putBoolean(IS_MAP_SAVED, true)
            }
            callback.invoke()
            return
        }
        synchronized(this) {
            if (oneFileReady) {
                callback.invoke()
                return
            } else {
                oneFileReady = true
            }
        }
    }

}