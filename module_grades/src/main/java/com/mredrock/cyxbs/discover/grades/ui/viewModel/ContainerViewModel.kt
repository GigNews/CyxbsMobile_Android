/**
 * @Author fxy
 * @Date 2019-12-10 20:44
 */

package com.mredrock.cyxbs.discover.grades.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.discover.grades.R
import com.mredrock.cyxbs.discover.grades.bean.Exam
import com.mredrock.cyxbs.discover.grades.bean.Status
import com.mredrock.cyxbs.discover.grades.bean.analyze.GPAStatus
import com.mredrock.cyxbs.discover.grades.network.ApiService
import io.reactivex.rxjava3.core.Observable
import retrofit2.HttpException

class ContainerViewModel : BaseViewModel() {
    companion object {
        const val STATUS_OK = "10000"
        const val ERROR = "10010"
    }

    //监听从bindactivity回来containerativity方便用户观看成绩则将bottom展开，如果是在本来在containeractivity则折叠
    val bottomStateListener = MutableLiveData<Boolean?>()

    //当前采取的考试展示策略
    val nowStatus = MutableLiveData<Status>()

    fun isContainerActivity() {
        bottomStateListener.postValue(false)
    }

    val examData = MutableLiveData<List<Exam>>()
    private val apiService = ApiGenerator.getApiService(ApiService::class.java)
    fun loadData(stuNum: String) {
        val exam = apiService.getExam(stuNum)
        val reExam = apiService.getReExam(stuNum)
        Observable.merge(exam, reExam)
            .setSchedulers()
            .mapOrThrowApiException()
            .doOnErrorWithDefaultErrorHandler {
                toastEvent.value = R.string.grades_no_exam_history
                false
            }
            .safeSubscribeBy {
                examData.value = it
            }.lifeCycle()
    }

    /**
     * 解绑ids
     */
    fun unbindIds(onSuccess: () -> Unit) {
        apiService.unbindIds()
            .doOnNext {
            }
            .doOnError {
            }
            .setSchedulers()
            .safeSubscribeBy(
                onNext = {
                    bottomStateListener.postValue(true)
                    BaseApp.appContext.toast(R.string.grades_bottom_sheet_unbind_success)
                    onSuccess.invoke()
                    isBinding.value = false
                },
                onError = {
                    BaseApp.appContext.toast(R.string.grades_bottom_sheet_unbind_fail)
                }
            ).lifeCycle()
    }

    /**
     * 当前是否处于绑定状态
     * 通过该LiveData改变按钮的点击事件
     */
    val isBinding = MutableLiveData<Boolean>()

    private val _analyzeData = MutableLiveData<GPAStatus>()
    val analyzeData: LiveData<GPAStatus>
        get() = _analyzeData

    fun getAnalyzeData() {
        apiService.getAnalyzeData()
            .setSchedulers()
            .safeSubscribeBy(
                onNext = {
                    _analyzeData.value = it
                    isBinding.value = true
                },
                onError = {
                    //未绑定的话,会导致状态码为400，Retrofit无法回调onNext
                    //详见：https://www.cnblogs.com/fuyaozhishang/p/8607706.html
                    if (it is HttpException) {
                        val errorBody = it.response()?.errorBody()?.string() ?: ""
                        try {
                            // 防止后端返回的status不符合json格式报错
                            val gpaStatus = Gson().fromJson(errorBody, GPAStatus::class.java)
                            _analyzeData.postValue(gpaStatus)
                            isBinding.value = false
                        } catch (e: Exception) {
                            BaseApp.appContext.toast("加载绩点失败")
                        }
                    } else {
                        //此时说明是一些其他的错误
                        val s =
                            "{\"errcode\":\"10010\",\"errmessage\":\"errCode:114514 errMsg: runtime error: index out of range [-1]\"}"
                        val gpaStatus = Gson().fromJson(s, GPAStatus::class.java)
                        _analyzeData.postValue(gpaStatus)
                        isBinding.value = false
                        BaseApp.appContext.toast("加载绩点失败")
                    }
                }
            ).lifeCycle()
    }

    //获取当前采取的成绩展示方案
    fun getStatus() {
        apiService.getNowStatus()
            .setSchedulers()
            .safeSubscribeBy {
                it.data.let { status ->
                    nowStatus.postValue(status)
                }
            }
    }

}