package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.bean.NoClassGroup
import com.mredrock.cyxbs.noclass.bean.Student
import com.mredrock.cyxbs.noclass.page.adapter.NoClassAddToGroupAdapter
import com.mredrock.cyxbs.noclass.page.viewmodel.fragment.SolidViewModel
import com.mredrock.cyxbs.noclass.widget.SeekBarView

/**
 * 将该学生添加到指定的分组中
 */

class AddToGroupDialog(
    private val groupList: List<NoClassGroup>,
    private val student: Student
) : BottomSheetDialogFragment() {

    /**
     * 分组文字的recyclerView
     */
    private lateinit var mRv: RecyclerView

    /**
     * seekbar
     */
    private lateinit var mSb: SeekBarView

    /**
     * 完成点击按钮
     */
    private lateinit var mBtnDone: Button

    /**
     * 选中的分组
     */
    private var chooseGroup: List<NoClassGroup>? = null

    /**
     * 固定分组fragment的viewModel
     */
    private val mSolidViewModel by viewModels<SolidViewModel>()

    /**
     * 剩余人员数量
     */
    private var restNum: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.noclass_dialog_add_to_group)
        initView(dialog)
        initRv()
        initScroll()
        initObserver()
        return dialog
    }

    private fun initObserver() {
        mSolidViewModel.addMembers.observe(this) {
            if (!it.second) {
                toast("添加失败")
            } else if (restNum == 0) {
                toast("添加成功")
                dismiss()
            }
            restNum--
        }
    }

    private fun initRv() {
        mRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 3, RecyclerView.HORIZONTAL, false)
            adapter = NoClassAddToGroupAdapter(groupList, context) { isOk, chooseGroup ->
                //true为可以点击完成,也就是深色
                if (isOk) {
                    mBtnDone.setBackgroundResource(R.drawable.noclass_shape_button_common_bg)
                    //更新分组
                    this@AddToGroupDialog.chooseGroup = chooseGroup
                    mBtnDone.isClickable = true
                } else {
                    mBtnDone.setBackgroundResource(R.drawable.noclass_shape_button_save_default_bg)
                    mBtnDone.isClickable = false
                }
            }
        }
    }


    private fun initView(dialog: Dialog) {
        mRv = dialog.findViewById(R.id.noclass_add_to_group_rv_container)
        mSb = dialog.findViewById(R.id.noclass_add_to_group_seekbar)
        // 弹窗的取消按钮
        dialog.findViewById<TextView>(R.id.noclass_add_to_group_tv_cancel).apply {
            setOnClickListener {
                dismiss()
            }
        }
        // 完成按钮
        mBtnDone = dialog.findViewById<Button>(R.id.noclass_add_to_group_done).apply {
            setOnClickListener {
                restNum = (chooseGroup!!.size - 2).coerceAtLeast(0)
                chooseGroup!!.forEach {
                    //每一个被选中的分组添加成员
                    mSolidViewModel.addMembers(it.id, setOf(student))
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.noclass_sheet_dialog_style)
    }

    /**
     * 这个方法用来rv和seekbar协作滑动，滑动事件监听
     */
    private fun initScroll() {
        mRv.apply {
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val offset = computeHorizontalScrollOffset().toFloat()
                    val range = computeHorizontalScrollRange().toFloat()
                    val extent = computeHorizontalScrollExtent().toFloat()
                    mSb.doMove(offset / (range - extent))
                }
            })
        }

        // 等待布局完成之后
        mRv.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            if (mRv.width >= resources.displayMetrics.widthPixels) {
                mSb.visible()
            }
        }
    }
}