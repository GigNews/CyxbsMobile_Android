package com.cyxbs.pages.mine.util.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.widget.TextView
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.cyxbs.pages.mine.R
import com.cyxbs.pages.mine.page.security.activity.FindPasswordActivity
import com.cyxbs.pages.mine.page.security.activity.FindPasswordActivity.Companion.FIND_PASSWORD_BY_EMAIL
import com.cyxbs.pages.mine.page.security.activity.FindPasswordActivity.Companion.FIND_PASSWORD_BY_SECURITY_QUESTION
import com.cyxbs.pages.mine.page.security.activity.FindPasswordByIdsActivity

/**
 * Author: RayleighZ
 * Time: 2020-11-03 2:34
 */
class ChooseFindTypeDialog(context: Context, theme: Int) : Dialog(context, theme) {
    companion object {
        //此处函数将来可以优化，目前必须要传递一个学号进来
        fun showDialog(context: Context?, hasEmailBinding: Boolean, hasSecurityQuestion: Boolean, activity: BaseActivity, isFromLogin: Boolean, stuNumber: String) {
            if (context == null) return
            val chooseFindTypeDialog = ChooseFindTypeDialog(context, R.style.transparent_dialog)
            chooseFindTypeDialog.setContentView(R.layout.mine_dialog_choose_find_type)
            chooseFindTypeDialog.window?.attributes?.gravity = Gravity.CENTER
            val tvEmail = chooseFindTypeDialog.findViewById<TextView>(R.id.mine_tv_dialog_choose_type_email)
            val tvProtect = chooseFindTypeDialog.findViewById<TextView>(R.id.mine_tv_dialog_choose_type_protect)
            val tvIds = chooseFindTypeDialog.findViewById<TextView>(R.id.mine_tv_dialog_choose_type_ids)
            tvEmail.setOnSingleClickListener {
                //当点击通过邮箱找回的按钮时
                if (hasEmailBinding) {
                    //启动邮箱找回模块
                    if (isFromLogin) {//是否已经登陆
                        FindPasswordActivity.actionStartFromLogin(context, FIND_PASSWORD_BY_EMAIL, stuNumber)
                    } else {
                        FindPasswordActivity.actionStartFromMine(context, FIND_PASSWORD_BY_EMAIL)
                    }
                    chooseFindTypeDialog.hide()
                    activity.finish()
                    chooseFindTypeDialog.dismiss()
                } else {
                    //弹出toast提示没有进行密码绑定
                    toast("您好像还没有绑定邮箱")
                }
            }
            tvProtect.setOnSingleClickListener {
                //当点击通过密保找回时
                if (hasSecurityQuestion) {
                    //启动密保找回模块
                    if (isFromLogin) {//是否已经登陆
                        FindPasswordActivity.actionStartFromLogin(context, FIND_PASSWORD_BY_SECURITY_QUESTION, stuNumber)
                    } else {
                        FindPasswordActivity.actionStartFromMine(context, FIND_PASSWORD_BY_SECURITY_QUESTION)
                    }
                    chooseFindTypeDialog.hide()
                    activity.finish()
                    chooseFindTypeDialog.dismiss()
                } else {
                    toast("您好像还没有设置密保问题")
                }
            }
            tvIds.setOnSingleClickListener {
                //当点击通过教务在线找回时，跳转至教务在线找回界面
                context.startActivity(Intent(context, FindPasswordByIdsActivity::class.java))
                chooseFindTypeDialog.hide()
                activity.finish()
                chooseFindTypeDialog.dismiss()
            }
            chooseFindTypeDialog.show()
        }
    }
}