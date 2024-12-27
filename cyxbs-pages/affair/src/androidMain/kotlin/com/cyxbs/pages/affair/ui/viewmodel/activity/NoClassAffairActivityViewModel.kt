package com.cyxbs.pages.affair.ui.viewmodel.activity

import androidx.lifecycle.viewModelScope
import com.cyxbs.components.base.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class NoClassAffairActivityViewModel : BaseViewModel() {

    private val _clickAffect = MutableSharedFlow<Unit>()
    val clickAffect: SharedFlow<Unit>
        get() = _clickAffect

    val clickBack : SharedFlow<Unit> get() = _clickBack
    private val _clickBack = MutableSharedFlow<Unit>()

    val changeBtn : SharedFlow<Int> get() = _changBtn
    private val _changBtn = MutableSharedFlow<Int>()

    fun clickNextBtn() {
        viewModelScope.launch {
            _clickAffect.emit(Unit)
        }
    }

    /**
     * 目前仅支持没课越
     */
    fun clickLastBtn(){
        viewModelScope.launch {
            _clickBack.emit(Unit)
        }
    }

    /**
     * 目前仅支持没课越
     * 用于用户选择标题之后按钮变色
     *
     * 1 -------------> 点击首页的标题之后按钮需要变色
     *
     * 2 -------------> 点击第二页按钮需要改变为发送通知(灰色)
     *
     * 3 -------------> 在第三页中点击按钮发送成功之后跳转到消息中心页面
     *
     * 4 -------------> 在第三页中点击返回键需要改变发送通知为下一步
     *
     * 5 -------------> 第三页将按钮变为可以点击的积极颜色(蓝色)
     */
    fun setBtnBg(pageNum : Int){
        viewModelScope.launch {
            _changBtn.emit(pageNum)
        }
    }

}