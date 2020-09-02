package com.mredrock.cyxbs.qa.pages.answer.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mredrock.cyxbs.common.bean.RedrockApiWrapper
import com.mredrock.cyxbs.common.bean.isSuccessful
import com.mredrock.cyxbs.common.config.QA_ANSWER_LIST
import com.mredrock.cyxbs.common.event.OpenShareQuestionEvent
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.common.viewmodel.event.ProgressDialogEvent
import com.mredrock.cyxbs.qa.R
import com.mredrock.cyxbs.qa.bean.Answer
import com.mredrock.cyxbs.qa.bean.Question
import com.mredrock.cyxbs.qa.component.recycler.RvAdapterWrapper
import com.mredrock.cyxbs.qa.network.NetworkState
import com.mredrock.cyxbs.qa.pages.answer.ui.dialog.ReportDialog
import com.mredrock.cyxbs.qa.pages.answer.viewmodel.AnswerListViewModel
import com.mredrock.cyxbs.qa.pages.comment.ui.CommentListActivity
import com.mredrock.cyxbs.qa.ui.adapter.EmptyRvAdapter
import com.mredrock.cyxbs.qa.ui.adapter.FooterRvAdapter
import kotlinx.android.synthetic.main.qa_activity_answer_list.*
import kotlinx.android.synthetic.main.qa_common_toolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = QA_ANSWER_LIST)
class AnswerListActivity : BaseActivity(), EventBusLifecycleSubscriber {
    companion object {
        @JvmField
        val TAG: String = AnswerListActivity::class.java.simpleName

        const val PARAM_QUESTION = "question"
        const val REQUEST_REFRESH_LIST = 0x2
        const val REQUEST_REFRESH_QUESTION_ADOPTED = "adopted"
        const val REQUEST_REFRESH_ANSWER_REFRESH = "answerRefresh"

        fun activityStart(fragment: Fragment, question: Question, requestCode: Int) {
            fragment.startActivityForResult<AnswerListActivity>(requestCode, PARAM_QUESTION to question)
        }
    }

    private lateinit var viewModel: AnswerListViewModel
    private var progressDialog: ProgressDialog? = null
    private fun initProgressBar() = ProgressDialog(this).apply {
        isIndeterminate = true
        setTitle("Loading...")
        setOnDismissListener { viewModel.onProgressDialogDismissed() }
    }

    override val isFragmentActivity = false

    private lateinit var headerAdapter: AnswerListHeaderAdapter
    private lateinit var answerListAdapter: AnswerListAdapter
    private lateinit var footerRvAdapter: FooterRvAdapter
    private lateinit var emptyRvAdapter: EmptyRvAdapter
    private lateinit var questionReportDialog: ReportDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qa_activity_answer_list)
        if (intent.getParcelableExtra<Question>(PARAM_QUESTION) != null) {
            initViewModel(intent.getParcelableExtra(PARAM_QUESTION))
            initView(intent.getParcelableExtra(PARAM_QUESTION))
            viewModel.addQuestionView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }

    private fun initViewModel(question: Question) {
        viewModel = ViewModelProvider(this, AnswerListViewModel.Factory(question)).get(AnswerListViewModel::class.java)
        viewModel.apply {
            toastEvent.observe { str -> str?.let { toast(it) } }
            longToastEvent.observe { str -> str?.let { longToast(it) } }
            progressDialogEvent.observe {
                it ?: return@observe
                if (it != ProgressDialogEvent.DISMISS_DIALOG_EVENT && progressDialog?.isShowing != true) {
                    progressDialog = progressDialog ?: initProgressBar()
                    progressDialog?.show()
                } else if (it == ProgressDialogEvent.DISMISS_DIALOG_EVENT && progressDialog?.isShowing != false) {
                    progressDialog?.dismiss()
                }
            }
        }
    }

    private fun initView(question: Question) {
        initRv()
        //设置标题
        qa_tv_toolbar_title.text = question.title
        qa_ib_toolbar_back.setOnClickListener { finish() }
        observeListChangeEvent()
        questionReportDialog = createQuestionReportDialog()
        if (question.isSelf) {
            //不展示更多功能
            qa_ib_toolbar_more.gone()
        } else {
            qa_ib_toolbar_more.setOnClickListener {
                doIfLogin {
                    questionReportDialog.show()
                }
            }
        }
    }

    private fun initRv() {
        swipe_refresh_layout.setOnRefreshListener {
            viewModel.invalidate()
        }
        headerAdapter = AnswerListHeaderAdapter()
        answerListAdapter = AnswerListAdapter().apply {
            onItemClickListener = {
                if (viewModel.questionLiveData.value != null) {
                    CommentListActivity.activityStart(this@AnswerListActivity, viewModel.questionLiveData.value!!, it)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            }
            onPraiseClickListener = { i: Int, answer: Answer ->
                if (viewModel.isDealing) {
                    toast(getString(R.string.qa_answer_praise_dealing))
                } else {
                    viewModel.clickPraiseButton(i, answer)
                    viewModel.apply {
                        refreshPreActivityEvent.observeNotNull {
                            answerListAdapter.notifyItemChanged(it)
                        }
                    }
                }
            }
            onReportClickListener = { id ->
                ReportDialog(this@AnswerListActivity).apply {
                    setType(resources.getStringArray(R.array.qa_title_type)[1])
                    pressReport = {
                        viewModel.reportAnswer(it, id)
                    }
                    viewModel.reportAnswerEvent.observeNotNull {
                        dismiss()
                    }
                }.show()
            }
        }
        footerRvAdapter = FooterRvAdapter { viewModel.retry() }
        emptyRvAdapter = EmptyRvAdapter(getString(R.string.qa_answer_list_no_answer_hint))
        val adapterWrapper = RvAdapterWrapper(answerListAdapter, headerAdapter, footerRvAdapter, emptyRvAdapter)
        rv_answer_list.apply {
            layoutManager = LinearLayoutManager(this@AnswerListActivity)
            adapter = adapterWrapper
        }
    }

    private inline fun <T> LiveData<T>.observe(crossinline onChange: (T?) -> Unit) = observe(this@AnswerListActivity, Observer { onChange(it) })

    private inline fun <T> LiveData<T>.observeNotNull(crossinline onChange: (T) -> Unit) = observe(this@AnswerListActivity, Observer {
        it ?: return@Observer
        onChange(it)
    })

    private fun observeListChangeEvent() = viewModel.apply {
        getMyReward()

        questionLiveData.observeNotNull {
            headerAdapter.refreshData(listOf(it))
        }

        bottomViewEvent.observe {
            when {
                it == null -> btn_answer.gone()
                it -> btn_answer.gone()
                else -> switchToHelper()
            }
        }
        answerPagedList.observeNotNull {
            answerListAdapter.submitList(it)
        }
        networkState.observeNotNull { footerRvAdapter.refreshData(listOf(it)) }

        initialLoad.observe {
            when (it) {
                NetworkState.LOADING -> {
                    swipe_refresh_layout.isRefreshing = true
                    emptyRvAdapter.showHolder(3)
                }
                else -> {
                    swipe_refresh_layout.isRefreshing = false
                    emptyRvAdapter.hideHolder()
                }
            }
        }

        backAndRefreshPreActivityEvent.observeNotNull {
            if (it) {
                val data = Intent()
                data.putExtra("type", questionLiveData.value?.kind)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }
    }

    private fun switchToHelper() {
        btn_answer.visible()
        btn_answer.setOnClickListener {
            doIfLogin {
                AnswerActivity.activityStart(this@AnswerListActivity, viewModel.questionLiveData.value?.id
                        ?: "", viewModel.questionLiveData.value?.description
                        ?: "", viewModel.questionLiveData.value?.photoUrl
                        ?: listOf(), REQUEST_REFRESH_LIST)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }


    }

    private fun createQuestionReportDialog() = ReportDialog(this@AnswerListActivity).apply {
        setType(resources.getStringArray(R.array.qa_title_type)[0])
        pressReport = {
            viewModel.reportQuestion(it)
        }
        viewModel.reportQuestionEvent.observeNotNull {
            dismiss()
        }
        viewModel.ignoreEvent.observeNotNull {
            dismiss()
        }
        pressQuestionIgnore = {
            viewModel.ignoreQuestion()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_REFRESH_LIST -> {
                    viewModel.invalidate()
                }
                CommentListActivity.REQUEST_CODE -> {
                    val question = viewModel.questionLiveData.value!!
                    if (data?.getIntExtra(REQUEST_REFRESH_ANSWER_REFRESH, 0) == 1) {
                        viewModel.invalidate()
                    }
                    if (data?.getIntExtra(REQUEST_REFRESH_QUESTION_ADOPTED, 0) == 1) {
                        question._hasAdoptedAnswer = 1
                        headerAdapter.refreshData(listOf(question))
                    }
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun openShareQuestion(event: OpenShareQuestionEvent) {
        if (intent.getParcelableExtra<Question>(PARAM_QUESTION) != null) {
            EventBus.getDefault().removeStickyEvent(event)
        } else {
            var questionWrapper: RedrockApiWrapper<Question>? = null
            try {
                questionWrapper = Gson().fromJson<RedrockApiWrapper<Question>>(event.questionJson,
                        object : TypeToken<RedrockApiWrapper<Question>>() {}.type)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (questionWrapper == null) {
                toast(getString(R.string.qa_question_from_mine_loading_error))
                finish()
            } else if (!questionWrapper.isSuccessful) {
                toast(questionWrapper.info
                        ?: getString(R.string.qa_loading_from_mine_unknown_error))
                finish()
            } else {
                initViewModel(questionWrapper.data)
                initView(questionWrapper.data)
                viewModel.addQuestionView()
            }
        }
    }
}