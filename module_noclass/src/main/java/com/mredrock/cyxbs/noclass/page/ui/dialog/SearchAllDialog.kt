package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.CLASS
import com.mredrock.cyxbs.noclass.bean.Cls
import com.mredrock.cyxbs.noclass.bean.GROUP
import com.mredrock.cyxbs.noclass.bean.GroupDetail
import com.mredrock.cyxbs.noclass.bean.NoClassItem
import com.mredrock.cyxbs.noclass.bean.NoClassTemporarySearch
import com.mredrock.cyxbs.noclass.bean.STUDENT
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.adapter.TemporarySearchAdapter
import com.mredrock.cyxbs.noclass.page.viewmodel.dialog.SearchAllDialogViewModel

/**
 * 搜索所有，包括学生，分组，班级
 * @param searchResult 用于展示结果
 * @param groupId 用来判断是临时分组还是组内管理，组内管理的添加需要网络请求才能添加成功！
 */
class SearchAllDialog(
    private val searchResult: NoClassTemporarySearch,
    private val groupId: String = "-1"
) : BottomSheetDialogFragment() {

    // 仅在组内管理界面添加人员需要用到
    private val mViewModel by viewModels<SearchAllDialogViewModel>()

    /**
     * 等待添加的缓冲区,后面是String类型，student，class还是group
     */
    private val mWaitAdd: HashMap<Set<Student>, String> by lazy { HashMap() }

    /**
     * 搜索结果的adapter
     */
    private val mAdapter by lazy { TemporarySearchAdapter() }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.noclass_dialog_search_student)
        val mScreenWidth: Int = resources.displayMetrics.widthPixels
        val mScreenHeight: Int = resources.displayMetrics.heightPixels
        dialog.findViewById<ConstraintLayout>(R.id.test_ccc).apply {
            this.layoutParams.height = mScreenHeight
            this.layoutParams.width = mScreenWidth
            requestLayout()
        }
        initView(dialog)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        if (dialog is BottomSheetDialog) {
            val behaviour = (dialog as BottomSheetDialog).behavior
            behaviour.isDraggable = false
            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.noclass_sheet_dialog_style)
        initObserve()
    }

    private fun initObserve() {
        mViewModel.addMembers.observe(this) {
            //如果成功了才真正添加到里面
            Log.d("lx", "stuNum=${it.first},isSuccess = ${it.second} ")
            if (it.second) {
                // 需要看看对应mWaitAdd的哪个
                for (key in mWaitAdd.keys) {
                    val stuNumSet = key.map { it.id }.toSet()
                    Log.d("lx", "stuNumSet=${stuNumSet} ")
                    if (it.first.containsAll(stuNumSet)) {
                        // 这个添加到组内管理界面
                        Log.d("lx", "key = ${key} ")
                        onClickGroupDetailAdd?.invoke(key.toList())
                        // 接下来判断当前dialog是否取消
                        if (searchResult.types.size == 2) {
                            dismiss()
                            break
                        }
                        // 此时绝对只有一条数据
                        when (searchResult.types[0]) {
                            STUDENT -> dismiss()
                            CLASS -> dismiss()
                            GROUP -> {
                                // 如果是分组的话，会有整体的分组和成员的item，所以如果点击group的加号才取消
                                if (mWaitAdd[key] == GROUP) {
                                    dismiss()
                                } else if (mWaitAdd[key] == STUDENT) {
                                    key.forEach { mAdapter.deleteStudent(it) }
                                }
                            }
                        }
                        break
                    }
                }
            } else {
                toast("添加失败~")
            }
        }
    }

    private fun initView(dialog: Dialog) {
        dialog.findViewById<TextView>(R.id.tv_noclass_search_student_cancel).apply {
            setOnClickListener {
                dialog.cancel()
            }
        }
        //点击空白处取消
        dialog.setCancelable(true)
        //设置dialog中的rv
        dialog.findViewById<RecyclerView>(R.id.rv_noclass_search_container).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter.apply {
                val data = searchResult
                val searchResultList = ArrayList<NoClassItem>()
                // 是否只有分组
                var isOnlyGroup = true
                //遍历类型，可能为学生，分组，班级，也可能为学生和分组重名组合
                if (groupId == "-1") {
                    for (type in data.types) {
                        when (type) {
                            STUDENT -> {
                                //如果分组id为-1，说明从临时分组界面过来，不进行网络请求直接添加。
                                setOnClickStudent {
                                    onClickStudent?.invoke(it)
                                    dialog.cancel()
                                }
                                isOnlyGroup = false
                                searchResultList.addAll(data.students)
                            }

                            CLASS -> {
                                setOnClickClass {
                                    onClickClass?.invoke(it)
                                    dialog.cancel()
                                }
                                isOnlyGroup = false
                                searchResultList.add(data.`class`)
                            }

                            GROUP -> {
                                //能够到这里就只剩分组了，所以如果只有分组，则显示学生，并且会覆盖对student的点击事件
                                setOnClickGroup {
                                    onClickGroup?.invoke(it)
                                    dialog.cancel()
                                }
                                searchResultList.add(data.group)
                                // 如果只有分组，此时才显示分组下面的学生
                                if (isOnlyGroup) {
                                    searchResultList.addAll(data.group.members)
                                    //单独设置的原因是因为要求点击分组下面的组员要求弹窗不消失
                                    setOnClickStudent {
                                        onClickStudent?.invoke(it)
                                        deleteStudent(it)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (type in data.types) {
                        when (type) {
                            STUDENT -> {
                                //如果分组id为-1，说明从临时分组界面过来，不进行网络请求直接添加。
                                setOnClickStudent {
                                    // 添加至缓冲区
                                    val stuSet = setOf(it)
                                    mWaitAdd[stuSet] = STUDENT
                                    // 进行网络请求增加组内成员
                                    mViewModel.addMembers(groupId, stuSet)
                                    isOnlyGroup = false
                                }
                                searchResultList.addAll(data.students)
                            }

                            CLASS -> {
                                setOnClickClass {
                                    // 添加至缓冲区
                                    val stuSet = it.members.toSet()
                                    mWaitAdd[stuSet] = CLASS
                                    // 进行网络请求增加班级内成员
                                    mViewModel.addMembers(groupId, stuSet)
                                    isOnlyGroup = false
                                }
                                searchResultList.add(data.`class`)
                            }

                            GROUP -> {
                                setOnClickGroup {
                                    val stuSet = it.members.toSet()
                                    mWaitAdd[stuSet] = GROUP
                                    mViewModel.addMembers(groupId, stuSet)
                                }
                                searchResultList.add(data.group)
                                // 如果只有分组，此时才显示分组下面的学生
                                if (isOnlyGroup) {
                                    setOnClickStudent {
                                        val stuSet = setOf(it)
                                        mWaitAdd[stuSet] = STUDENT
                                        mViewModel.addMembers(groupId, stuSet)
                                    }
                                    searchResultList.addAll(data.group.members)
                                }
                            }
                        }
                    }
                }
                submitList(searchResultList)
            }
        }
    }

    //点击学生的回调
    private var onClickStudent: ((stu: Student) -> Unit)? = null

    fun setOnClickStudent(onClickStudent: (stu: Student) -> Unit) {
        this.onClickStudent = onClickStudent
    }

    //点击group的回调
    private var onClickGroup: ((groupDetail: GroupDetail) -> Unit)? = null

    fun setOnClickGroup(onClickGroup: (groupDetail: GroupDetail) -> Unit) {
        this.onClickGroup = onClickGroup
    }

    //点击class的回调
    private var onClickClass: ((cls: Cls) -> Unit)? = null

    fun setOnClickClass(onClickClass: (cls: Cls) -> Unit) {
        this.onClickClass = onClickClass
    }

    //固定分组中点击添加之后的回调
    private var onClickGroupDetailAdd: ((stuList: List<Student>) -> Unit)? = null
    fun setOnClickGroupDetailAdd(onClickGroupDetailAdd: ((stuList: List<Student>) -> Unit)) {
        this.onClickGroupDetailAdd = onClickGroupDetailAdd
    }
}