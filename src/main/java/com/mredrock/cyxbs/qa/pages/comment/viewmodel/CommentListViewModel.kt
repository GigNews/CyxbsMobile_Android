package com.mredrock.cyxbs.qa.pages.comment.viewmodel

import android.arch.lifecycle.*
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.util.Base64
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.network.ApiGenerator
import com.mredrock.cyxbs.common.utils.extensions.checkError
import com.mredrock.cyxbs.common.utils.extensions.safeSubscribeBy
import com.mredrock.cyxbs.common.utils.extensions.setSchedulers
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.common.viewmodel.event.SingleLiveEvent
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.bean.Comment
import com.mredrock.cyxbs.qa.network.ApiService
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.comment.model.CommentDataSource

class CommentListViewModel(val qid: String,
                           answer: Answer) : BaseViewModel() {
    private val factory: CommentDataSource.Factory
    val commentList: LiveData<PagedList<Comment>>
    val networkState: LiveData<Int>
    val initialLoad: LiveData<Int>

    val answerLiveData = MutableLiveData<Answer>()
    val isPraised = Transformations.map(this.answerLiveData) { it.isPraised }!!
    val praiseCount = Transformations.map(this.answerLiveData) { it.praiseNum }!!
    val commentCount = Transformations.map(this.answerLiveData) { it.commentNum }!!

    val refreshPreActivityEvent = SingleLiveEvent<Boolean>()

    private var praiseNetworkState = NetworkState.SUCCESSFUL

    init {
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(3)
                .setPageSize(6)
                .setInitialLoadSizeHint(6)
                .build()
        factory = CommentDataSource.Factory(answer.id)
        commentList = LivePagedListBuilder<Int, Comment>(factory, config).build()
        networkState = Transformations.switchMap(factory.commentDataSourceLiveData) { it.networkState }
        initialLoad = Transformations.switchMap(factory.commentDataSourceLiveData) { it.initialLoad }

        answerLiveData.value = answer
    }

    fun invalidateCommentList() = factory.commentDataSourceLiveData.value?.invalidate()

    fun retryFailedListRequest() = factory.commentDataSourceLiveData.value?.retry()

    fun adoptAnswer(aId: String) {
        ApiGenerator.getApiService(ApiService::class.java)
                .adoptAnswer(aId, qid,
                        BaseApp.user?.stuNum ?: "",
                        BaseApp.user?.idNum ?: "")
                .checkError()
                .setSchedulers()
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .safeSubscribeBy {
                    answerLiveData.value?.isAdopted = true
                    refreshPreActivityEvent.value = true
                }
    }

    fun clickPraiseButton() {
        fun Boolean.toInt() = 1.takeIf { this@toInt } ?: -1

        if (praiseNetworkState == NetworkState.LOADING) {
            toastEvent.value = R.string.qa_comment_list_comment_loading_hint
            return
        }

        val user = BaseApp.user!!
        val answer = answerLiveData.value!!

        ApiGenerator.getApiService(ApiService::class.java)
                .run {
                    if (answer.isPraised) {
                        cancelPraiseAnswer(answer.id, user.stuNum!!, user.idNum!!)
                    } else {
                        praiseAnswer(answer.id, user.stuNum!!, user.idNum!!)
                    }
                }
                .checkError()
                .setSchedulers()
                .doOnSubscribe {
                    val state = !answer.isPraised
                    (isPraised as MutableLiveData).value = state
                    (praiseCount as MutableLiveData).value = "${answer.praiseNumInt + state.toInt()}"
                }
                .doOnError {
                    (isPraised as MutableLiveData).value = answer.isPraised
                    (praiseCount as MutableLiveData).value = answer.praiseNum
                    toastEvent.value = R.string.qa_service_error_hint
                }
                .doFinally { praiseNetworkState = NetworkState.SUCCESSFUL }
                .safeSubscribeBy {
                    answer.apply {
                        val state = !isPraised
                        praiseNum = "${answer.praiseNumInt + state.toInt()}"
                        isPraised = state
                    }
                    refreshPreActivityEvent.value = true
                }
                .lifeCycle()
    }

    fun sendComment(content: String) {
        if (content.isBlank()) {
            longToastEvent.value = R.string.qa_comment_dialog_error_input_hint
            return
        }
        val user = BaseApp.user!!
        val answer = answerLiveData.value ?: return
        ApiGenerator.getApiService(ApiService::class.java)
                .sendComment(answer.id, content, user.stuNum!!, user.idNum!!)
                .checkError()
                .setSchedulers()
                .doOnSubscribe { progressDialogEvent.value = ProgressDialogEvent.SHOW_NONCANCELABLE_DIALOG_EVENT }
                .doFinally { progressDialogEvent.value = ProgressDialogEvent.DISMISS_DIALOG_EVENT }
                .safeSubscribeBy {
                    answerLiveData.value = answer.apply { commentNum = "${commentNumInt + 1}" }
                    refreshPreActivityEvent.value = true
                    invalidateCommentList()
                }
                .lifeCycle()
    }

    fun saveToDraft(content: String?) {
        if (content.isNullOrBlank()) {
            return
        }
        val user = BaseApp.user ?: return
        val s = "{\"title\":\"$content\"}"
        val json = Base64.encodeToString(s.toByteArray(), Base64.DEFAULT)
        ApiGenerator.getApiService(ApiService::class.java)
                .addItemToDraft(user.stuNum ?: "", user.idNum
                        ?: "", "remark", json, answerLiveData.value!!.id)
                .setSchedulers()
                .checkError()
                .safeSubscribeBy(
                        onError = {
                            toastEvent.value = R.string.qa_quiz_save_failed
                        },
                        onNext = {
                            toastEvent.value = R.string.qa_quiz_save_success
                        }
                )
                .lifeCycle()
    }

    class Factory(private val qid: String,
                  private val answer: Answer) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(CommentListViewModel::class.java)) {
                return CommentListViewModel(qid, answer) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found.")
            }
        }
    }
}
