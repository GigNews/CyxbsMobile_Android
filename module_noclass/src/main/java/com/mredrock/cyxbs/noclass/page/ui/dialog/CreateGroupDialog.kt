package com.mredrock.cyxbs.noclass.page.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.postDelayed
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.toast
import com.mredrock.cyxbs.lib.utils.service.ServiceManager
import com.mredrock.cyxbs.noclass.R
import com.mredrock.cyxbs.noclass.page.viewmodel.other.CreateGroupViewModel
import com.mredrock.cyxbs.noclass.util.startShake

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.ui
 * @ClassName:      CreateGroupDialogFragment
 * @Author:         Yan
 * @CreateDate:     2022年08月15日 17:23:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    创建新分组的bottom sheet dial og
 * @param existName 如果是固定分组界面，已有分组，可空
 * @param afterCreate 固定分组创建成功之后的操作
 *
 */
class CreateGroupDialog(
    val afterCreate: (() -> Unit)? = null
) : BottomSheetDialogFragment() {

    private val mViewModel by viewModels<CreateGroupViewModel>()

    /**
     * 用来存储成员信息的
     */
    private var mExtraNums: List<String>? = null

    /**
     * 用户名称
     */
    private lateinit var mUserName: String

    /**
     * 用户id
     */
    private lateinit var mUserId: String

    /**
     * 创建分组的名称
     */
    private var mGroupName: String? = null

    /**
     * 输入错误的隐藏文字
     */
    private lateinit var mTvHint: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.noclass_dialog_create_group)
        initView(dialog)
        return dialog
    }

    /**
     * 观察
     */
    private fun initObserve() {
        // 上传分组的观察者
        mViewModel.isCreateSuccess.observe(this) {
            if (it.id == -1) {
                toast("似乎出现了什么问题呢,请稍后再试")
            } else if (it.id == -2) {
                mTvHint.text = "和已有分组重名，再想想吧"
                mTvHint.visibility = View.VISIBLE
                createUndone(mTvHint)
            } else {
                toast("创建成功")
                // 唤醒传进来的创建完成之后的方法
                afterCreate?.invoke()
                dialog?.cancel()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.noclass_sheet_dialog_style)
        initUserInfo()
        initObserve()
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

    private fun initView(dialog: Dialog) {
        //分组名称textview
        val tvName = dialog.findViewById<TextView>(R.id.tv_noclass_create_group_name)
        //创建分组名称dialog
        val etName = dialog.findViewById<EditText>(R.id.et_noclass_group_name)
        //创建完成按钮上方的提示
        mTvHint = dialog.findViewById<TextView>(R.id.tv_noclass_create_group_hint).apply {
            visibility = View.INVISIBLE
        }
        //取消textview
        dialog.findViewById<TextView>(R.id.tv_noclass_create_group_cancel).apply {
            setOnClickListener {
                dialog.cancel()
            }
        }

        //TextWatcher监听
        etName.addTextChangedListener(
            onTextChanged = { s, _, before, _ ->
                if (s?.length == 0) {
                    etName.gravity = Gravity.NO_GRAVITY
                    tvName.visibility = View.VISIBLE
                    mTvHint.text = "请输入你的分组名称"
                    mTvHint.visibility = View.VISIBLE
                } else if (s?.length == 10) {
                    etName.gravity = Gravity.CENTER
                    tvName.visibility = View.GONE
                    mTvHint.postDelayed(3000) {
                        mTvHint.visibility = View.INVISIBLE
                    }
                    mTvHint.text = "分组名称不能超过10个字符"
                    mTvHint.visibility = View.VISIBLE
                } else {
                    etName.gravity = Gravity.CENTER
                    tvName.visibility = View.GONE
                    mTvHint.visibility = View.INVISIBLE
                }
            }
        )

        //创建完成的按钮
        dialog.findViewById<Button>(R.id.btn_noclass_group_create_done).apply {
            setOnSingleClickListener {
                if (etName.text.isEmpty()) {
                    createUndone(mTvHint)
                    mTvHint.text = "请输入你的分组名称"
                    mTvHint.visibility = View.VISIBLE
                } else {
                    val name = etName.text.toString()
                    //不为空直接调用
                    createDone(name)
                }
            }
        }
    }

    /**
     * 发送创建分组请求
     */
    private fun createDone(name: String) {
        // 存储着学号信息的字符串，用 , 分割
        var extraNums = ""
        mExtraNums?.forEachIndexed { index, num ->
            extraNums += num
            if (index != mExtraNums!!.size - 1) {
                extraNums += ","
            }
        }
        mViewModel.postNoclassGroup(name, extraNums)
    }

    /**
     * 创建失败
     */
    private fun createUndone(textView: TextView) {
        textView.startShake()
    }

    fun setExtraMembers(members: List<String>) {
        if (members.isNotEmpty()) {
            mExtraNums = members
        }
    }


}