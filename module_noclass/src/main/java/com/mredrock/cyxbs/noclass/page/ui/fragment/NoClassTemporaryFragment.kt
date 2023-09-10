package com.mredrock.cyxbs.noclass.page.ui.fragment


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.base.ui.BaseFragment
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.adapter.NoClassTemporaryAdapter
import com.mredrock.cyxbs.noclass.page.ui.dialog.SearchAllDialog
import com.mredrock.cyxbs.noclass.page.ui.dialog.SearchNoExistDialog
import com.mredrock.cyxbs.noclass.page.viewmodel.other.CourseViewModel
import com.mredrock.cyxbs.noclass.page.viewmodel.fragment.TemporaryViewModel
import com.mredrock.cyxbs.noclass.util.alphaAnim

/**
 * 临时分组的fragment
 */
class NoClassTemporaryFragment : BaseFragment(R.layout.noclass_fragment_temporary) {

    /**
     * 用户名称
     */
    private lateinit var mUserName: String

    /**
     * 用户id
     */
    private lateinit var mUserId: String

    /**
     * 查询按钮
     */
    private val mBtnQuery: Button by R.id.noclass_temporary_btn_query.view()

    /**
     * 上方添加同学的编辑框
     */
    private val mEditTextView: EditText by R.id.noclass_temporary_et_add_classmate.view()

    /**
     * 下面得提示文字，试试左滑删除列表
     */
    private val mHintText : TextView by R.id.noclass_temporary_tv_hint.view()

    /**
     * 临时分组界面展示人员的Rv和adapter
     */
    private val mRecyclerView: RecyclerView by R.id.noclass_temporary_rv_show.view()
    private val mAdapter by lazy { NoClassTemporaryAdapter() }

    private val mViewModel by viewModels<TemporaryViewModel>()
    private val mCourseViewModel by activityViewModels<CourseViewModel>()

    /**
     * 设置两秒后消失得runnable和handler，注意及时释放
     */
    private var mRunnable : Runnable? = null
    private var mHandler : Handler? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserInfo()
        initRv()
        initObserver()
        initHintText()
        initSearchEvent()
        initFindCourse()
    }

    /**
     * 初始化下面试试左滑删除列表，设置两秒后消失
     */
    private fun initHintText(content : String? = null) {
        mHintText.alpha = 1f
        mHintText.visible()
        content?.let { mHintText.text = it }
        mHandler = Handler(Looper.getMainLooper())
        mRunnable = Runnable {
            mHintText.alphaAnim(mHintText.alpha,0f,200).start()
            mHintText.gone()
        }
        mHandler!!.postDelayed(mRunnable!!,2000)
    }

    private fun initFindCourse() {
        //设置查询课表
        mBtnQuery.setOnSingleClickListener(interval = 1000) {
            doSearchCourse()
        }
    }

    /**
     * 初始化临时分组页面的rv
     */
    private fun initRv() {
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter.apply {
                //加入本人
                val list = listOf(Student("","","","",mUserName,mUserId,""))
                submitList(list)
                setOnItemDelete {
                    deleteMember(it)
                }
            }
        }
    }

    /**
     * 初始化用户信息
     */
    private fun initUserInfo() {
        ServiceManager.invoke(IAccountService::class).getUserService().apply {
            mUserName = this.getRealName()
            mUserId = this.getStuNum()
        }
    }


    /**
     * 执行查询空闲课程的操作
     */
    private fun doSearchCourse() {
        mCourseViewModel.getLessons(mAdapter.currentList.map { it.id }, mAdapter.currentList)
    }

    /**
     * 初始化观察者
     */
    private fun initObserver() {
        var searchAllDialog: SearchAllDialog?
        mViewModel.searchAll.observe(viewLifecycleOwner) {
            if (it != null && it.isSuccess()){
                if (it.data.types != null && it.data.types!!.isNotEmpty()) {
                    searchAllDialog = SearchAllDialog(it.data).apply {
                        setOnClickClass { cls ->
                            val clsList = mAdapter.currentList.toMutableSet()
                            clsList.addAll(cls.members)
                            mAdapter.submitList(clsList.toList())
                        }
                        setOnClickStudent { stu ->
                            val stuList = mAdapter.currentList.toMutableSet()
                            stuList.add(stu)
                            mAdapter.submitList(stuList.toList())
                        }
                        setOnClickGroup { group ->
                            val groupList = mAdapter.currentList.toMutableSet()
                            groupList.addAll(group.members)
                            mAdapter.submitList(groupList.toList())
                        }
                    }
                    searchAllDialog!!.show(childFragmentManager, "SearchAllDialog")
                } else {
                    SearchNoExistDialog(requireContext()).show()
                }
            }else{
                initHintText("网络异常请检查网络")
            }
        }
    }

    /**
     * 搜索操作的初始化
     */
    private fun initSearchEvent() {
        //防止软键盘弹起导致视图错位
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        //设置键盘上点击搜索的监听
        mEditTextView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearchStu()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    /**
     * 执行查找学生操作
     */
    private fun doSearchStu() {
        val content = mEditTextView.text.toString().trim()
        if (TextUtils.isEmpty(content)) {
            toast("输入为空")
            return
        }
        mViewModel.getSearchAllResult(content)
    }

    override fun onStop() {
        super.onStop()
        mRunnable?.let { mHandler?.removeCallbacks(it) }
        mHintText.gone()
    }


    companion object {
        @JvmStatic
        fun newInstance() = NoClassTemporaryFragment().apply {}
    }
}